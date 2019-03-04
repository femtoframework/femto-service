package org.femtoframework.service.apsis.local;

import org.femtoframework.net.message.MessageRegistry;
import org.femtoframework.net.message.ReqRepPair;
import org.femtoframework.net.message.RequestAware;
import org.femtoframework.net.message.RequestMessage;

/**
 * LocalReqRepPair
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-11-15 11:37:30
 */
public class LocalReqRepPair extends ReqRepPair
{
    private MessageRegistry registry = null;
    private boolean done = false;
    /**
     * 请求消息类型
     */
    private int reqMsgType;


    public LocalReqRepPair(Object request, MessageRegistry registry)
    {
        this(request, registry, registry.getType(request));
    }

    public LocalReqRepPair(Object request, MessageRegistry registry, int reqMsgType)
    {
        this.registry = registry;
        setRequest(request);
        this.reqMsgType = reqMsgType;
    }

    public boolean isDone()
    {
        return done;
    }

    void setDone(boolean done)
    {
        this.done = done;
    }

    /**
     * 任务完成
     * <p/>
     * 在请求完成之后调用，将响应写会给客户端
     */
    public void ack()
    {
        setDone(true);
        synchronized(this) { //通知所有的
            this.notifyAll();
        }
    }

    /**
     * 返回响应
     *
     * @return 响应
     */
    public Object getResponse()
    {
        if (response == null) {
            int repType = -reqMsgType;
            response = registry.createMessage(repType);
            if (response instanceof RequestAware) {
                ((RequestAware)response).setRequest(request);
            }
        }
        return response;
    }

    /**
     * 返回请求超时时间，如果请求超时时间是0，表示没有超时
     *
     * @return 请求超时时间
     */
    public int getTimeout()
    {
        if (request instanceof RequestMessage) {
            return ((RequestMessage)request).getTimeout();
        }
        return 0;
    }
}
