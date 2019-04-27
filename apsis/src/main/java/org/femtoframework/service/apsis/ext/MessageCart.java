package org.femtoframework.service.apsis.ext;


import org.femtoframework.net.message.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * 消息推车（等待执行）
 *
 * @author fengyun
 * @version 1.00 2005-5-22 16:28:53
 */
class MessageCart implements Runnable, Message
{
    MessageMetadata metadata;
    Object message;

    /**
     * 入队的时间
     */
    private long startTime;

    /**
     * 超时时间
     */
    private int timeout;

    /**
     * 是否是请求
     */
    private boolean isRequest = false;


    /**
     * 消息侦听者
     */
    MessageListener listener;

    /**
     * 消息执行的状态
     */
    Future future;


    private static final Logger log = LoggerFactory.getLogger("apsis:message_cart");

    /**
     * 构造
     *
     * @param metadata Metadata
     * @param message  消息
     */
    public MessageCart(MessageMetadata metadata, Object message, MessageListener listener)
    {
        this.metadata = metadata;
        this.message = message;
        this.listener = listener;
        //如果消息是请求，那么关注它在队列中的等待和执行时间
        if (message instanceof RequestMessage) {
            if (message instanceof RequestResponse) {
                id = ((RequestResponse) message).getId();
            }
            isRequest = true;
            startTime = System.currentTimeMillis();
            //请求在队列中的等待时间
            timeout = ((RequestMessage) message).getTimeout();
        }
    }

    /**
     * 判断消息是否已经超时
     *
     * @return 是否已经超时
     */
    public boolean isTimeout()
    {
        return isRequest && timeout > 0 && System.currentTimeMillis() - startTime > timeout;
    }

    //消息标识
    private int id = -1;
    private Map<Integer, MessageCart> runningMessages = null;

    /**
     * 添加到执行的列表中去
     *
     * @param runningMessages
     */
    public void addToRunning(Map<Integer, MessageCart> runningMessages)
    {
        if (id != -1) {
            this.runningMessages = runningMessages;
            synchronized (runningMessages) {
                runningMessages.put(id, this);
            }
            //把时间戳改成当前的时间，用来记录执行的情况
            startTime = System.currentTimeMillis();
        }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run()
    {
        try {
            listener.onMessage(metadata, message);
        }
        catch (Throwable t) {
            log.warn("Throwable", t);
        }
        finally {
            if (runningMessages != null) {
                synchronized (this) {
                    runningMessages.remove(id);
                }
            }
        }
    }

    /**
     * 尝试取消任务
     */
    public void cancel()
    {
        if (future != null) {
            future.cancel(true);
        }
    }

    /**
     * 返回消息的序列号，循环使用<br>
     *
     * @return 序列号
     */
    public int getId()
    {
        return id;
    }
}
