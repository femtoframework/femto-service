package org.femtoframework.service.rmi;

import java.lang.reflect.Method;
import java.rmi.Remote;

/**
 * 远程调用
 *
 * @author fengyun
 * @version 1.00 2005-5-21 3:04:14
 */
public interface RemoteInvocation
{
    /**
     * Invoke a method. This form of delegating method invocation
     * to the reference allows the reference to take care of
     * setting up the connection to the remote host, marshaling
     * some representation for the method and parameters, then
     * communicating the method invocation to the remote host.
     * This method either returns the result of a method invocation
     * on the remote object which resides on the remote host or
     * throws a RemoteException if the call failed or an
     * application-level exception if the remote invocation throws
     * an exception.
     *
     * @param vmid   虚拟机标识
     * @param oid    对象标识
     * @param obj    the object that contains the RemoteRef (e.g., the
     *               RemoteStub for the object.
     * @param method the method to be invoked
     * @param params the parameter list
     * @param opnum  a hash that may be used to represent the method
     * @return result of remote method invocation
     * @throws Exception if any exception occurs during remote method
     *                   invocation
     * @since 1.2
     */
    Object invoke(long vmid, ObjID oid, Remote obj, Method method, Object[] params, long opnum)
        throws Exception;
}
