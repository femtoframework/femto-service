package org.femtoframework.service.apsis.balance;

import org.femtoframework.coin.CoinConstants;
import org.femtoframework.coin.naming.CoinNamingParser;
import org.femtoframework.lang.reflect.Reflection;
import org.femtoframework.service.apsis.balance.rmi.BalanceInterceptor;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.util.HashSet;

/**
 * 负载均衡工具类
 *
 * @author fengyun
 * @version 1.00 2005-8-6 16:48:18
 */
public class BalanceUtil
{
    private static CoinNamingParser parser = new CoinNamingParser(CoinConstants.CHAR_SLASH);

    /**
     * 根据期望类型，接口数组，远程URI产生负载均衡Stub对象
     *
     * @param expectedType 期望类型
     * @param interfaces   接口数组
     * @param uri          远程URI，如果是 APSIS 格式的名称，请采用 apsis://SERVER_PART/SERVICE_PART/MODEL_PART的格式
     * @param loader       类装载器
     * @return 产生的Stub对象
     */
    public static Object generate(String expectedType, ClassLoader loader, String uri, String... interfaces)
        throws Exception
    {
        int index = uri.indexOf("://");
        String scheme = null;
        if (index < 0) {
            scheme = "coin";
            index = 0;
        }
        else {
            scheme = uri.substring(0, index);
            index += 3; // "://"
        }
        int h = uri.indexOf('/', index);
        if (h < 0) {
            throw new InvalidNameException("No '/' after '://' " + uri);
        }
        String server = uri.substring(index, h);
        if (server.indexOf(':') > 0) {
            //如果仅仅是 host + ":" + port 那么添加'!'
            if (!server.startsWith("!")) {
                server = '!' + server;
            }
        }
        else if (server.length() > 1) {
            if (!server.startsWith("#")) {
                server = "#" + server;
            }
        }
        Name path = parser.parse(uri.substring(h + 1));

        HashSet<Class> unique = new HashSet<Class>();
        if (expectedType != null) {
            Class clazz = Reflection.loadClass(loader, expectedType);
            if (clazz.isInterface()) {
                unique.add(clazz);
            }
            Class[] interfaceClasses;
            while (clazz != Object.class && clazz != null) {
                interfaceClasses = clazz.getInterfaces();
                for (int i = 0, len = interfaceClasses.length; i < len; i++) {
                    unique.add(interfaceClasses[i]);
                }
                clazz = clazz.getSuperclass();
            }
        }
        if (interfaces != null) {
            for (int i = 0, len = interfaces.length; i < len; i++) {
                unique.add(Reflection.loadClass(loader, interfaces[i]));
            }
        }
        //add Remote interface automatic 
        unique.add(Remote.class);

        InvocationHandler interceptor = new BalanceInterceptor(scheme, server, path);
        Class[] interfaceClasses = new Class[unique.size()];
        unique.toArray(interfaceClasses);
        return Proxy.newProxyInstance(loader, interfaceClasses, interceptor);
    }

    /**
     * 根据期望类型，接口数组，远程URI产生负载均衡Stub对象
     *
     * @param expectedType 期望类型
     * @param interfaces   接口数组
     * @param uri          远程URI
     * @return 产生的Stub对象
     */
    public static Object generate(String expectedType, String uri, String... interfaces)
        throws Exception
    {
        return generate(expectedType, Reflection.getClassLoader(), uri, interfaces);
    }
}
