package org.femtoframework.service.apsis.balance.rmi;

import org.bolango.apsis.ApsisBalancer;
import org.bolango.apsis.ApsisClient;
import org.bolango.apsis.ApsisSessionID;
import org.bolango.apsis.SessionLocal;
import org.bolango.apsis.client.MultiClient;
import org.bolango.apsis.coin.CoinNamingContext;
import org.bolango.apsis.naming.ApsisName;
import org.bolango.apsis.naming.ApsisNamingConstants;
import org.bolango.apsis.naming.ApsisNamingContext;
import org.bolango.frame.SessionID;
import org.bolango.frame.client.ClientUtil;
import org.bolango.frame.rmi.ConnectException;
import org.bolango.naming.NamingConstants;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.rmi.NoSuchObjectException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 负载均衡动态代理的拦截机
 *
 * @author fengyun
 * @version 1.00 2005-8-6 16:00:23
 */
public class BalanceInterceptor implements InvocationHandler {
    /**
     * 服务器信息部分
     */
    private String server;

    /**
     * Scheme
     */
    private String scheme;

    /**
     * 路径信息
     */
    private Name path;

    /**
     * 服务器标识到Interceptor的影射
     */
    private final Map<Long, InvocationHandler> interceptors;

    /**
     * 负载均衡器
     */
    private final ApsisBalancer balancer;

    /**
     * 构造
     *
     * @param scheme Scheme
     * @param server 服务器信息
     * @param path   路径信息
     */
    public BalanceInterceptor(String scheme, String server, Name path) {
        this.scheme = scheme;
        this.server = server;
        this.path = path;
        this.balancer = ClientUtil.getBalancer();
        this.interceptors = new HashMap<Long, InvocationHandler>();
    }

    /**
     * Processes a method invocation on a proxy instance and returns
     * the result.  This method will be invoked on an invocation handler
     * when a method is invoked on a proxy instance that it is
     * associated with.
     *
     * @param proxy  the proxy instance that the method was invoked on
     * @param method the <code>Method</code> instance corresponding to
     *               the interface method invoked on the proxy instance.  The declaring
     *               class of the <code>Method</code> object will be the interface that
     *               the method was declared in, which may be a superinterface of the
     *               proxy interface that the proxy class inherits the method through.
     * @param args   an array of objects containing the values of the
     *               arguments passed in the method invocation on the proxy instance,
     *               or <code>null</code> if interface method takes no arguments.
     *               Arguments of primitive types are wrapped in instances of the
     *               appropriate primitive wrapper class, such as
     *               <code>java.lang.Integer</code> or <code>java.lang.Boolean</code>.
     * @return the value to return from the method invocation on the
     *         proxy instance.  If the declared return type of the interface
     *         method is a primitive type, then the value returned by
     *         this method must be an instance of the corresponding primitive
     *         wrapper class; otherwise, it must be a type assignable to the
     *         declared return type.  If the value returned by this method is
     *         <code>null</code> and the interface method's return type is
     *         primitive, then a <code>NullPointerException</code> will be
     *         thrown by the method invocation on the proxy instance.  If the
     *         value returned by this method is otherwise not compatible with
     *         the interface method's declared return type as described above,
     *         a <code>ClassCastException</code> will be thrown by the method
     *         invocation on the proxy instance.
     * @throws Throwable the exception to throw from the method
     *                   invocation on the proxy instance.  The exception's type must be
     *                   assignable either to any of the exception types declared in the
     *                   <code>throws</code> clause of the interface method or to the
     *                   unchecked exception types <code>java.lang.RuntimeException</code>
     *                   or <code>java.lang.Error</code>.  If a checked exception is
     *                   thrown by this method that is not assignable to any of the
     *                   exception types declared in the <code>throws</code> clause of
     *                   the interface method, then an
     *                   {@link java.lang.reflect.UndeclaredThrowableException} containing the
     *                   exception that was thrown by this method will be thrown by the
     *                   method invocation on the proxy instance.
     * @see java.lang.reflect.UndeclaredThrowableException
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) { //Object 自身的方法，直接调用代理的
            if ("toString".equals(method.getName())) {
                return server + "/" + path;
            }
            else if ("equals".equals(method.getName())) {
                return (server + "/" + path).equals(String.valueOf(args[0]));
            }
        }
        SessionID sid = SessionLocal.getSessionID();
        ApsisSessionID asid = sid instanceof ApsisSessionID ? (ApsisSessionID)sid : null;
        ApsisClient client = balancer.apsisBalance(new ApsisName(server + "/" + path), asid, null, null);
        if (client == null) {
            //没有可用的服务器
            throw new ConnectException("Can't connect to " + server + "/" + path + " sid:" + asid);
        }

        if (client instanceof MultiClient) {
            MultiClient multiClient = (MultiClient)client;
            List<ApsisClient> clients = multiClient.getClients();
            if (clients.isEmpty()) {
                throw new ConnectException("No live server " + server + "/" + path);
            }

            MultiClientInvocation invocation = new MultiClientInvocation(multiClient, server, path.toString());
            return invocation.invoke(proxy, method, args);
        }
        else {
            return doInvoke(client, proxy, method, args);
        }
    }

    protected Object doInvoke(ApsisClient client, Object proxy, Method method, Object[] args) throws Throwable {
        InvocationHandler interceptor = getInterceptor(client);
        try {
            return interceptor.invoke(proxy, method, args);
        } catch (NoSuchObjectException nsoe) {
            //远程对象不存在了，重新获取
            interceptors.remove(client.getId());

            interceptor = getInterceptor(client);
            try {
                return interceptor.invoke(proxy, method, args);
            } catch (NoSuchObjectException nsoe2) {
                //还出错就是系统故障
                throw nsoe2;
            }
        }
    }

    protected InvocationHandler getInterceptor(ApsisClient client)
        throws Exception {
        long vmid = client.getId();
        InvocationHandler interceptor = interceptors.get(vmid);
        if (interceptor == null) {
            synchronized (interceptors) {
                interceptor = interceptors.get(vmid);
                if (interceptor == null) {
                    interceptor = createInterceptor(client);
                    interceptors.put(vmid, interceptor);
                }
            }
        }
        return interceptor;
    }

    protected InvocationHandler createInterceptor(ApsisClient client)
        throws Exception {
        String host = client.getHost();
        int port = client.getPort();
        String baseUri = createBaseUri(host, port);

        URI uri = new URI(baseUri);

        Hashtable env = new Hashtable();
        env.put(NamingConstants.PROVIDER_URI, uri);

        Context base = null;
        if (ApsisNamingConstants.SCHEME.equals(scheme)) {
            base = new ApsisNamingContext(env, "", null);
        }
        else if ("coin".equals(scheme)) {
            base = new CoinNamingContext(env, "", null);
        }
        else {
            throw new NamingException("Unsupported scheme:" + scheme);
        }

        Object obj = base.lookup(path);

        if (obj == null) {
            throw new ConnectException("No such object of the server :" + client + " baseUri:" + baseUri
                                       + " Object:" + path);
        }
        try {
            return Proxy.getInvocationHandler(obj);
        }
        catch (Exception ex) {
            return new StubInterceptor(obj);
        }
    }

    private String createBaseUri(String host, int port) {
        return scheme + "://" + host + ":" + port;
    }
}
