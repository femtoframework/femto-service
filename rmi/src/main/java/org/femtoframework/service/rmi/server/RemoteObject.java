package org.femtoframework.service.rmi.server;

import org.femtoframework.service.rmi.RemoteRef;
import org.femtoframework.service.rmi.ServerRef;
import org.femtoframework.service.rmi.UnicastRef;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.MarshalException;
import java.rmi.Remote;

/**
 * 远程对象<br>
 * 与java.rmi.server.RemoteObject是兼容的，继承哪个类都可以导出<br>
 *
 * @author fengyun
 * @version 1.00 Mar 29, 2003 4:12:05 AM
 */
public class RemoteObject implements Remote, Externalizable {

    protected transient RemoteRef ref;

    protected RemoteObject() {
        ref = null;
    }

    protected RemoteObject(RemoteRef ref) {
        this.ref = ref;
    }

    public void setRef(RemoteRef ref) {
        this.ref = ref;
    }


    public void writeExternal(ObjectOutput out)
        throws IOException {
        if (ref == null) {
            throw new MarshalException("Invalid remote object");
        }
        else {
            boolean isServer = ref instanceof ServerRef;
            out.writeBoolean(isServer);
            ref.writeExternal(out);
        }
    }

    public void readExternal(ObjectInput in)
        throws IOException, ClassNotFoundException {
        boolean isServer = in.readBoolean();
        if (isServer) {
            ref = new UnicastServerRef();
        }
        else {
            ref = new UnicastRef();
        }
        ref.readExternal(in);
    }

    public String toString() {
        String className = this.getClass().getName();
        return (ref == null) ? className :
               className + " {" + ref.remoteToString() + "}";
    }
}
