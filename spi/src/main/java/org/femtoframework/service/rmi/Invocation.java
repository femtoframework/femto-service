package org.femtoframework.service.rmi;

import java.rmi.RemoteException;

/**
 * 调用指定的方法
 *
 * @author fengyun
 * @version Feb 13, 2003 8:51:02 PM
 */
public interface Invocation
{
    /**
     * 调用方法
     *
     * @param obj    the target remote object for the call
     * @param params the parameter list
     * @param opnum  a hash that may be used to represent the method
     *               method arguments can be obtained.
     * @throws RemoteException unable to marshal
     * @return result
     */
    Object invoke(Object obj, Object[] params, long opnum) throws RemoteException;
}
