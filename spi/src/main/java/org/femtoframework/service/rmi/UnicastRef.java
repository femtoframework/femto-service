package org.femtoframework.service.rmi;

import org.femtoframework.implement.ImplementUtil;
import org.femtoframework.net.HostPort;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.rmi.Remote;

/**
 * Unicast引用
 *
 * @author fengyun
 * @version Feb 13, 2003 5:39:02 PM
 */

public class UnicastRef implements RemoteRef {
    /**
     * 对象邦定的主机和端口
     */
    protected long vmid;

    /**
     * 对象标识
     */
    protected ObjID id = null;

    /**
     * 远程调用实现
     */
    private static final RemoteInvocation invocation = ImplementUtil.getInstance(RemoteInvocation.class);


    private transient String host;
    private transient int port;

    /**
     * Create a new (empty) Unicast remote reference.
     */
    public UnicastRef() {
    }

    /**
     * Create a new (empty) Unicast remote reference.
     */
    public UnicastRef(long vmid, ObjID id) {
        this.vmid = vmid;
        this.id = id;
    }

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
    public Object invoke(Remote obj, Method method,
                         Object[] params, long opnum)
        throws Exception {
        return invocation.invoke(vmid, id, obj, method, params, opnum);
    }

    /**
     * Returns a hashcode for a remote object.  Two remote object stubs
     * that refer to the same remote object will have the same hash code
     * (in order to support remote objects as keys in hash tables).
     *
     * @return remote object hashcode
     * @see java.util.Hashtable
     * @since JDK1.1
     */
    public int remoteHashCode() {
        return id.hashCode();
    }

    /**
     * Compares two remote objects for equality.
     * Returns a boolean that indicates whether this remote object is
     * equivalent to the specified Object. This method is used when a
     * remote object is stored in a hashtable.
     *
     * @param ref the Object to compare with
     * @return true if these Objects are equal; false otherwise.
     * @see java.util.Hashtable
     * @since JDK1.1
     */
    public boolean remoteEquals(RemoteRef ref) {
        if (ref != null && ref instanceof UnicastRef) {
            UnicastRef ur = (UnicastRef)ref;
            return vmid == ur.vmid && id.equals(ur.id);
        }
        return false;
    }

    /**
     * Returns a String that represents the reference of this remote
     * object.
     *
     * @return string representing remote object reference
     * @since JDK1.1
     */
    public String remoteToString() {
        if (host == null) {
            HostPort hp = HostPort.toHostPort(vmid);
            host = hp.getHost();
            port = hp.getPort();
        }
        return "RemoteStub [ref://" + host + ":" + port + "/" + id + "]";
    }
    
    @Override
    public void writeExternal(ObjectOutput oos) throws IOException {
        oos.writeLong(vmid);
//        if (id instanceof LongOID) {
//            oos.writeByte(0);
//        }
//        else { //StrOID
            oos.writeByte(1);
//        }
        id.writeExternal(oos);
    }

    @Override
    public void readExternal(ObjectInput ois) throws IOException, ClassNotFoundException {
        vmid = ois.readLong();
        ois.readByte();
//        if (b == 0) {
//            id = new LongOID(0);
//        }
//        else {
            id = new StrOID();
//        }
        id.readExternal(ois);
    }
}
