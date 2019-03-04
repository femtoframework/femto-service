package org.femtoframework.service.apsis.local;

import org.femtoframework.net.message.MessageFuture;

/**
 * LocalMessageFuture
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-11-15 11:40:10
 */
public class LocalMessageFuture implements MessageFuture
{
    /**
     * 是否取消消息，如果消息还在队列中的化那么删除该消息，如果消息
     * 正在处理，那么根据mayInterruptIfRunning参数来决定是否中断处理
     *
     * @param mayInterruptIfRunning 如果已经在处理是否中断该消息
     * @return 是否取消成功
     */
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        return false;
    }

    /**
     * 是否已经取消的
     *
     * @return 是否已经取消
     */
    public boolean isCancelled()
    {
        return false;
    }

    /**
     * 消息是否已经完成发送
     *
     * @return 是否已经完成发送
     */
    public boolean isDone()
    {
        return true;
    }
}
