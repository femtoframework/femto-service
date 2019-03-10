package org.femtoframework.service.apsis.balance.rmi;

import org.femtoframework.net.message.RequestFuture;
import org.femtoframework.net.message.RequestTimeoutException;
import org.femtoframework.service.apsis.ApsisClient;
import org.femtoframework.service.apsis.client.MultiClient;
import org.femtoframework.service.apsis.client.MultiRequestFuture;
import org.femtoframework.service.apsis.rmi.RmiRequest;
import org.femtoframework.service.apsis.rmi.RmiResponse;
import org.femtoframework.service.rmi.RmiUtil;
import org.femtoframework.service.rmi.StrOID;
import org.femtoframework.service.rmi.server.RemoteInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationException;
import java.rmi.server.ServerCloneException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xshao on 2/1/16.
 */
public class MultiClientInvocation implements InvocationHandler {

    private MultiClient multiClient;

    private String path;

    private String server;

    public MultiClientInvocation(MultiClient multiClient, String server, String path)
    {
        this.multiClient = multiClient;
        this.server = server;
        this.path = path;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        long opnum = RemoteInterceptor.getMethodHash(method);
        RmiRequest request = new RmiRequest();

        request.setObjID(new StrOID(path));
        request.setMethod(opnum);
        request.setArguments(args);

        List<ApsisClient> clientList = multiClient.getClients();

        List<RequestFuture> futureList = new ArrayList<RequestFuture>(clientList.size());

        for(ApsisClient client: clientList) {
            int timeout = RmiUtil.getTimeout();
            request.setTimeout(timeout);
            RequestFuture future = client.submit(request);
            futureList.add(future);
        }


        int timeout = RmiUtil.getTimeout();

        RmiResponse response = null;
        if ("^".equals(server)) {
            response = getFirstValidResponse(futureList, timeout);
        }
        else {
            MultiRequestFuture multiRequestFuture = new MultiRequestFuture(futureList);
            response = (RmiResponse)(timeout > 0 ? multiRequestFuture.getResponse(timeout)
                    : multiRequestFuture.getResponse());
        }

        boolean isEx = response.isException();
        Object result = response.getResult();
        if (isEx && result instanceof Exception) {
            throw exceptionReceivedFromServer((Exception) result);
        }
        else {
            return result;
        }
    }

    private RmiResponse getFirstValidResponse(List<RequestFuture> futureList, long timeout) throws InterruptedException {
        int size = futureList.size();
        if (timeout <= 0) { // 20 Seconds
            timeout = 20 * 1000l;
        }
        RmiResponse responses[] = new RmiResponse[size];
        long to = timeout / size;
        long start = System.currentTimeMillis();
        boolean allEx = true;
        while((System.currentTimeMillis()-start) < timeout) {
            boolean allDone = true;
            for (int i = 0; i < size; i++) {
                RequestFuture rf = futureList.get(i);
                if (rf.isDone()) {
                    responses[i] = (RmiResponse)rf.getResponse(to);
                    if (!responses[i].isException()) {
                        Object result = responses[i].getResult();
                        if (result != null) { //Not null, return it
                            return responses[i];
                        }
                        allEx = false;
                    }
                }
                else {
                    allDone = false;
                }
            }

            if (allDone) {
                break;
            }
        }

        if (allEx) {
            for (int i = 0; i < size; i++) {
                if (responses[i] != null) {
                    return responses[i];
                }
            }
        }
        else {
            for (int i = 0; i < size; i++) {
                if (responses[i] != null && !responses[i].isException()) {
                    return responses[i];
                }
            }
        }
        throw new RequestTimeoutException("Request timeout, no any return");
    }

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
}
