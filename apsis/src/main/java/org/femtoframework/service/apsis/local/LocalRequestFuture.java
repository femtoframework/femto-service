package org.femtoframework.service.apsis.local;

import org.femtoframework.net.message.RequestFuture;
import org.femtoframework.net.message.RequestTimeoutException;
import org.femtoframework.net.message.ResponseListener;

/**
 * LocalRequestFuture
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-11-15 11:48:48
 */
public class LocalRequestFuture extends LocalMessageFuture implements RequestFuture
{
    private LocalReqRepPair reqRepPair;

    public LocalRequestFuture(LocalReqRepPair reqRepPair)
    {
        this.reqRepPair = reqRepPair;
    }

    /**
     * 等待响应返回，直到有响应返回或者当前的线程被中断
     *
     * @return 响应
     * @throws RequestCancellationException
     *                              if the computation was cancelled
     * @throws InterruptedException if the current thread was interrupted
     *                              while waiting
     */
    public Object getResponse() throws InterruptedException
    {
        return getResponse(reqRepPair.getTimeout());
    }

    /**
     * 等待指定的时间，直到响应返回
     *
     * @param timeout the maximum time to wait（单位毫秒）
     * @return the computed result
     * @throws RequestCancellationException
     *                              if the computation was cancelled
     * @throws InterruptedException if the current thread was interrupted
     *                              while waiting
     */
    public Object getResponse(long timeout) throws InterruptedException, RequestTimeoutException
    {
        synchronized (reqRepPair) {
            if (timeout > 0) {
                while (!reqRepPair.isDone() && timeout > 0) {
                    timeout -= 100;
                    reqRepPair.wait(timeout);
                }
            }
            else if (reqRepPair.isDone()) {
                return reqRepPair.getResponse();
            }
            else {
                reqRepPair.wait();
            }
        }
        if (!reqRepPair.isDone()) {
            throw new RequestTimeoutException();
        }
        return reqRepPair.getResponse();
    }

    /**
     * 添加响应侦听者
     *
     * @param listener 响应侦听者
     */
    public void setResponseListener(ResponseListener listener)
    {

    }

    /**
     * 返回响应侦听者
     *
     * @return 响应侦听者
     */
    public ResponseListener getResponseListener()
    {
        return null;
    }
}