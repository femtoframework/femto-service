package org.femtoframework.service.apsis.client;

import org.femtoframework.net.message.RequestFuture;
import org.femtoframework.net.message.RequestTimeoutException;
import org.femtoframework.net.message.ResponseListener;

import java.util.List;

/**
 * MultiRequestFuture
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-11-30 14:12:32
 */
public class MultiRequestFuture implements RequestFuture
{
    private List<RequestFuture> rfs = null;
    private Object response = null;

    public MultiRequestFuture(List<RequestFuture> rfs)
    {
        this.rfs = rfs;
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
        return getResponse(20000);
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
        if (response == null) {
            int size = rfs.size();
            Object responses[] = new Object[size];
            long to = timeout / size;
            for (int i = 0; i < size; i++) {
                RequestFuture rf = rfs.get(i);
                responses[i] = rf.getResponse(to);
            }
            response = new MultiResponse(responses);
        }
        return response;
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

    /**
     * 是否取消消息，如果消息还在队列中的化那么删除该消息，如果消息
     * 正在处理，那么根据mayInterruptIfRunning参数来决定是否中断处理
     *
     * @param mayInterruptIfRunning 如果已经在处理是否中断该消息
     * @return 是否取消成功
     */
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        for (RequestFuture rf : rfs) {
            rf.cancel(mayInterruptIfRunning);
        }
        return true;
    }

    /**
     * 是否已经取消的
     *
     * @return 是否已经取消
     */
    public boolean isCancelled()
    {
        return rfs.get(0).isCancelled();
    }

    /**
     * 消息是否已经完成发送
     *
     * @return 是否已经完成发送
     */
    public boolean isDone()
    {
        return response != null;
    }
}
