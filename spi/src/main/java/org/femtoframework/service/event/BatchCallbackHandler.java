package org.femtoframework.service.event;

/**
 * 批处理回调处理器
 *
 * @author fengyun
 * @version 1.00 2006-10-18 14:11:14
 */
public class BatchCallbackHandler
    implements EventCallbackHandler
{
    private int totalCount;

    private int handledCount = 0;

//    private static final Logger log = LogUtil.getLogger("event/callback/handler");

    private final Object lock;

    private int timeout = 0;

    private long startTime;

    public BatchCallbackHandler(Object lock)
    {
        this(lock, 0);
    }

    public BatchCallbackHandler(Object lock, int timeout)
    {
        this.lock = lock;
        this.timeout = timeout;
        this.startTime = System.currentTimeMillis();
    }

    public void reset(int totalCount)
    {
        this.handledCount = 0;
        this.startTime = System.currentTimeMillis();
        setTotalCount(totalCount);
    }

    public void callback(EventCallback callback)
    {
//        if (callback.isException()) {
//            Throwable e = callback.getException();
//            log.error("task_error", e);
//            return;
//        }

        done();

        if (isDone()) {
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

    public void setTotalCount(int totalCount)
    {
        this.totalCount = totalCount;
    }

    public int getTotalCount()
    {
        return this.totalCount;
    }

    public synchronized void done()
    {
        handledCount++;
    }

    public boolean isDone()
    {
        return handledCount >= totalCount || isTimeout();
    }

    public boolean isTimeout()
    {
        return timeout > 0 && System.currentTimeMillis() - startTime > timeout;
    }

    public int getTimeout()
    {
        return timeout;
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    public int getHandledCount()
    {
        return handledCount;
    }
}
