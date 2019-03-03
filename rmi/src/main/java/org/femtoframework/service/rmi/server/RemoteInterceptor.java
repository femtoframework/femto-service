package org.femtoframework.service.rmi.server;

import org.femtoframework.service.rmi.RemoteException;
import org.femtoframework.service.rmi.RemoteRef;

import java.io.InvalidObjectException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 远程方法调用拦截机
 *
 * @author fengyun
 * @version 1.00 2005-5-21 13:50:52
 */
public class RemoteInterceptor
    extends RemoteObject
    implements InvocationHandler
{
    private static final long serialVersionUID = 2L;

    /**
     * A weak hash map, mapping classes to weak hash maps that map
     * method objects to method hashes.
     */
    private static final MethodToHashMap METHOD_TO_HASH_MAP = new MethodToHashMap();

    public RemoteInterceptor() {

    }

    /**
     * Creates a new <code>RemoteObjectInvocationHandler</code> constructed
     * with the specified <code>RemoteRef</code>.
     *
     * @param ref the remote ref
     * @throws NullPointerException if <code>ref</code> is <code>null</code>
     */
    public RemoteInterceptor(RemoteRef ref)
    {
        super(ref);
        if (ref == null) {
            throw new NullPointerException();
        }
    }

    /**
     * Handles java.lang.Object methods.
     */
    private Object invokeObjectMethod(Object proxy,
                                      Method method,
                                      Object[] args)
        throws Throwable
    {
        String name = method.getName();

        if (name.equals("hashCode")) {
            return hashCode();
        }
        else if (name.equals("equals")) {
            Object obj = args[0];
            return proxy == obj ||
                   (obj != null &&
                    Proxy.isProxyClass(obj.getClass()) &&
                    equals(Proxy.getInvocationHandler(obj)));
        }
        else if (name.equals("toString")) {
            return proxyToString(proxy);
        }
        else {
            throw new IllegalArgumentException("unexpected Object method: " + method);
        }
    }

    /**
     * Handles remote methods.
     */
    private Object invokeRemoteMethod(Object proxy,
                                      Method method,
                                      Object[] args)
        throws Exception
    {
        try {
            if (!(proxy instanceof Remote)) {
                throw new IllegalArgumentException("proxy not Remote instance");
            }
            return ref.invoke((Remote) proxy, method, args, getMethodHash(method));
        }
        catch (Exception e) {
            if (e instanceof java.rmi.RemoteException) {
                //内部的远程异常，如果拥有可以抛出 java.rmi.RemoteException的参数，那么直接抛出
                Class[] exTypes = method.getExceptionTypes();
                for (Class<?> exType : exTypes) {
                    if (exType.isAssignableFrom(java.rmi.RemoteException.class)) {
                        //用java.rmi.RemoteException来抛出，而不是运行期异常
                        throw e;
//                        throw new java.rmi.RemoteException(re.getMessage(), re.getCause());
                    }
                }
            }
            else if (!(e instanceof RuntimeException)) {
                Class<?> cl = proxy.getClass();
                try {
                    method = cl.getMethod(method.getName(),
                                          method.getParameterTypes());
                }
                catch (NoSuchMethodException nsme) {
                    throw(IllegalArgumentException)
                        new IllegalArgumentException().initCause(nsme);
                }
                Class[] exTypes = method.getExceptionTypes();
                Class thrownType = e.getClass();
                for (Class<?> exType : exTypes) {
                    if (exType.isAssignableFrom(thrownType)) {
                        throw e;
                    }
                }
                e = new RemoteException("Remote exception", e);
            }
            else if (e instanceof RemoteException) {
                //内部的远程异常，如果拥有可以抛出 java.rmi.RemoteException的参数，那么直接抛出
                Class[] exTypes = method.getExceptionTypes();
                for (Class<?> exType : exTypes) {
                    if (exType.isAssignableFrom(java.rmi.RemoteException.class)) {
                        //用java.rmi.RemoteException来抛出，而不是运行期异常
//                        throw (java.rmi.RemoteException) e;
                        RemoteException re = (RemoteException)e;
                        throw new java.rmi.RemoteException(re.getMessage(), re.getCause());
                    }
                }
            }
            throw e;
        }
    }

    /**
     * Returns a string representation for a proxy that uses this invocation
     * handler.
     */
    private String proxyToString(Object proxy)
    {
        Class[] interfaces = proxy.getClass().getInterfaces();
        if (interfaces.length == 0) {
            return "Proxy[" + this + "]";
        }
        String iface = interfaces[0].getName();
        if (iface.equals("java.rmi.Remote") && interfaces.length > 1) {
            iface = interfaces[1].getName();
        }
        int dot = iface.lastIndexOf('.');
        if (dot >= 0) {
            iface = iface.substring(dot + 1);
        }
        return "Proxy[" + iface + "," + this + "]";
    }

    /**
     * @throws InvalidObjectException unconditionally
     */
    private void readObjectNoData() throws InvalidObjectException
    {
        throw new InvalidObjectException("no data in stream; class: " +
                                         this.getClass().getName());
    }

    /**
     * Returns the method hash for the specified method.  Subsequent calls
     * to "getMethodHash" passing the same method argument should be faster
     * since this method caches internally the result of the method to
     * method hash mapping.  The method hash is calculated using the
     * "computeMethodHash" method.
     *
     * @param method the remote method
     * @return the method hash for the specified method
     */
    public static long getMethodHash(Method method)
    {
        Map map = METHOD_TO_HASH_MAP.getMap(method.getDeclaringClass());
        return (Long) map.get(method);
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
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        if (method.getDeclaringClass() == Object.class) {
            return invokeObjectMethod(proxy, method, args);
        }
        else {
            return invokeRemoteMethod(proxy, method, args);
        }
    }

    /**
     * A weak hash map, mapping classes to weak hash maps that map
     * method objects to method hashes.
     */
    private static class MethodToHashMap extends WeakClassHashMap
    {

        MethodToHashMap()
        {
        }

        protected Map createMap(Class remoteClass)
        {
            return new WeakHashMap<Object, Long>()
            {
                public synchronized Long get(Object key)
                {
                    Long hash = super.get(key);
                    if (hash == null) {
                        Method method = (Method) key;
                        hash = MethodUtil.hashCode(method);
                        put(method, hash);
                    }
                    return hash;
                }
            };
        }
    }
}
