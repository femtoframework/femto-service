package org.femtoframework.service.rmi;

import java.io.Externalizable;
import java.lang.reflect.Method;
import java.rmi.Remote;

/**
 * 远程引用
 *
 * @author fengyun
 * @version Feb 13, 2003 5:40:07 PM
 */
public interface RemoteRef extends Externalizable
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
    Object invoke(Remote obj, Method method, Object[] params, long opnum)
        throws Exception;

    /**
     * Returns a hashcode for a remote object.  Two remote object stubs
     * that refer to the same remote object will have the same hash code
     * (in order to support remote objects as keys in hash tables).
     *
     * @return remote object hashcode
     * @see java.util.Hashtable
     * @since JDK1.1
     */
    int remoteHashCode();

    /**
     * Compares two remote objects for equality.
     * Returns a boolean that indicates whether this remote object is
     * equivalent to the specified Object. This method is used when a
     * remote object is stored in a hashtable.
     *
     * @param obj the Object to compare with
     * @return true if these Objects are equal; false otherwise.
     * @see java.util.Hashtable
     * @since JDK1.1
     */
    boolean remoteEquals(RemoteRef obj);

    /**
     * Returns a String that represents the reference of this remote
     * object.
     *
     * @return string representing remote object reference
     * @since JDK1.1
     */
    String remoteToString();
}
