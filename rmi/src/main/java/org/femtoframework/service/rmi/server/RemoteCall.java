package org.femtoframework.service.rmi.server;

import org.femtoframework.service.rmi.Invocation;
import org.femtoframework.service.rmi.ObjID;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * Remote Call
 *
 * @author fengyun
 * @version Feb 13, 2003 11:58:46 PM
 */
public class RemoteCall
{
    /**
     * 调用方法
     *
     * @param id     the id of the object
     * @param params the parameter list
     * @param opnum  a hash that may be used to represent the method
     *               method arguments can be obtained.
     * @throws RemoteException unable to marshal
     *                                  return result
     */
    public static Object invoke(ObjID id, final Object[] params, final long opnum)
        throws RemoteException
    {
        final Object impl;

        /* get the remote object */
        Target target = ObjectTable.getTarget(id);
        if (target == null || (impl = target.getImpl()) == null) {
            throw new NoSuchObjectException("No such object in table:" + id + " target:" + target);
        }

        final Invocation disp = target.getInvocation();
        target.incrementCallCount();
        try {
            AccessControlContext acc = target.getAccessControlContext();
            ClassLoader ccl = target.getContextClassLoader();

            Thread t = Thread.currentThread();
            ClassLoader savedCcl = t.getContextClassLoader();
            try {
                t.setContextClassLoader(ccl);
                try {
                    return AccessController.doPrivileged((PrivilegedExceptionAction) () -> disp.invoke(impl, params, opnum), acc);
                }
                catch (PrivilegedActionException pae) {
                    throw (RemoteException)pae.getException();
                }
            }
            finally {
                t.setContextClassLoader(savedCcl);
            }
        }
        finally {
            target.decrementCallCount();
        }
    }

    /**
     * 根据对象实现返回RemoteStub
     *
     * @param impl 实现
     * @return RemoteStub
     */
    public static Remote getStub(Remote impl)
    {
        Target target = ObjectTable.getTarget(impl);
        if (target != null) {
            return target.getStub();
        }
        return null;
    }
}
