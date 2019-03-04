package org.femtoframework.service.apsis.rmi;

import org.femtoframework.net.HostPort;
import org.femtoframework.net.message.RequestFuture;
import org.femtoframework.service.apsis.ApsisClient;
import org.femtoframework.service.client.ClientUtil;
import org.femtoframework.service.rmi.ConnectException;
import org.femtoframework.service.rmi.ObjID;
import org.femtoframework.service.rmi.RemoteInvocation;
import org.femtoframework.service.rmi.RmiUtil;
import org.femtoframework.service.rmi.server.RemoteCall;

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationException;
import java.rmi.server.ServerCloneException;

/**
 * 远程方法调用的实现
 *
 * @author fengyun
 * @version 1.00 2005-5-21 14:16:50
 */
public class SimpleRemoteInvocation implements RemoteInvocation {

    /**
     * Routine that causes the stack traces of remote exceptions to be
     * filled in with the current stack trace on the client.  Detail
     * exceptions are filled in iteratively.
     */
    protected Exception exceptionReceivedFromServer(Exception ex) {
        Throwable t = ex;
        while (t != null) {
            t.fillInStackTrace();
            if (t instanceof RemoteException) {
                t = ((RemoteException)t).detail;
            }
            else if (t instanceof ServerCloneException) {
                t = ((ServerCloneException)t).detail;
            }
            else if (t instanceof ActivationException) {
                t = ((ActivationException)t).detail;
            }
            else {
                break;
            }
        }
        return ex;
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
    public Object invoke(long vmid, ObjID oid, Remote obj, Method method, Object[] params, long opnum)
        throws Exception {
        if (vmid == HostPort.getLocal().getId()) {
            //本地
            return RemoteCall.invoke(oid, params, opnum);
        }
        else {
            RmiRequest request = new RmiRequest();
            request.setObjID(oid);
            request.setMethod(opnum);
            request.setArguments(params);

            ApsisClient client = ClientUtil.getClient(vmid, true);
            if (client == null) {
                throw new ConnectException("Can't connect to " + HostPort.toHostPort(vmid));
            }
            int timeout = RmiUtil.getTimeout();
            request.setTimeout(timeout);
            RequestFuture future = client.submit(request);
            RmiResponse response = (RmiResponse)(timeout > 0 ? future.getResponse(timeout) : future.getResponse());
            boolean isEx = response.isException();
            Object result = response.getResult();
            if (isEx && result instanceof Exception) {
                throw exceptionReceivedFromServer((Exception)result);
            }
            else {
                return result;
            }
        }
    }
}
