package org.femtoframework.service.rmi.server;

import org.femtoframework.service.rmi.*;

import java.io.Externalizable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.util.Collections;
import java.util.HashSet;

/**
 * Stub工具类
 *
 * @author fengyun
 * @version 1.00 2005-5-21 14:04:21
 */
public class StubUtil {
    /**
     * Returns a proxy for the specified implClass.
     * <p/>
     * If both of the following criteria is satisfied, a dynamic proxy for
     * the specified implClass is returned (otherwise a RemoteStub instance
     * for the specified implClass is returned):
     * <p/>
     * a) either the property java.rmi.server.ignoreStubClasses is true or
     * a pregenerated stub class does not exist for the impl class, and
     * b) forceStubUse is false.
     * <p/>
     * If the above criteria are satisfied, this method constructs a
     * dynamic proxy instance (that implements the remote interfaces of
     * implClass) constructed with a RemoteObjectInvocationHandler instance
     * constructed with the clientRef.
     * <p/>
     * Otherwise, this method loads the pregenerated stub class (which
     * extends RemoteStub and implements the remote interfaces of
     * implClass) and constructs an instance of the pregenerated stub
     * class with the clientRef.
     *
     * @param implClass the class to obtain remote interfaces from
     * @param clientRef the remote ref to use in the invocation handler
     * @throws IllegalArgumentException if implClass implements illegal
     *                                  remote interfaces
     */
    public static Remote createStub(Class<?> implClass, RemoteRef clientRef) {
        HashSet<Class<?>> unique = new HashSet<>();
        Class<?> clazz = implClass;
        Class<?>[] interfaces;
        while (clazz != Object.class) {
            interfaces = clazz.getInterfaces();
            Collections.addAll(unique, interfaces);
            clazz = clazz.getSuperclass();
        }
        //固定添加Remote
        unique.add(Remote.class);
        unique.remove(Externalizable.class);
        unique.add(RemoteStub.class);

        ClassLoader loader = implClass.getClassLoader();
        InvocationHandler interceptor = new RemoteInterceptor(clientRef);
        Class[] interfaceClasses = new Class[unique.size()];
        unique.toArray(interfaceClasses);
        return (Remote)Proxy.newProxyInstance(loader, interfaceClasses, interceptor);
    }

    /**
     * 创建Stub
     *
     * @param implClass 实现类
     * @param vmid      JVM标识
     * @param objId     对象标识
     * @return 远程Stub
     */
    public static Remote createStub(Class implClass, long vmid, ObjID objId) {
        return createStub(implClass, new UnicastRef(vmid, objId));
    }
}
