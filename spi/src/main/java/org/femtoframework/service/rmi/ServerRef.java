package org.femtoframework.service.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 服务器引用
 *
 * @author fengyun
 * @version 1.00 Mar 29, 2003 3:44:43 AM
 */
public interface ServerRef
    extends RemoteRef
{
    /**
     * Creates a client stub object for the supplied Remote object.
     * If the call completes successfully, the remote object should
     * be able to accept incoming calls from clients.
     *
     * @param obj  the remote object implementation
     * @param data information necessary to export the object
     * @return the stub for the remote object
     * @throws RemoteException if an exception occurs attempting
     *                                  to export the object (e.g., stub class could not be found)
     */
    Remote exportObject(Object obj, Object data) throws RemoteException;
}
