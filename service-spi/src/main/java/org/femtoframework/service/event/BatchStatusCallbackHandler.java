package org.femtoframework.service.event;

/**
 * 批处理状态处理器
 *
 * @author fengyun
 * @version 1.00 2006-10-24 19:03:13
 */
public class BatchStatusCallbackHandler
    extends BatchCallbackHandler
    implements EventStatusCallbackHandler
{
    public BatchStatusCallbackHandler(Object lock)
    {
        super(lock);
    }

    public BatchStatusCallbackHandler(Object lock, int timeout)
    {
        super(lock, timeout);
    }
}
