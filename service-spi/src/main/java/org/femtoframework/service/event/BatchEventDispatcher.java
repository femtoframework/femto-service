package org.femtoframework.service.event;

import org.femtoframework.service.client.ClientUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 批处理分发器
 *
 * @author fengyun
 * @version 1.00 2006-12-5 13:09:39
 */
public abstract class BatchEventDispatcher<A> {
    // 投递同步消息的地址
    private String uri;

    /**
     * 超时时间
     */
    private int timeout = 300000;

    /**
     * 一个任务中处理帐户的大小
     */
    private int batchItemSize = 100;

    /**
     * 批处理任务数目
     */
    private int batchTaskCount = 50;

    /**
     * 数据源
     */
    private BatchDataSource dataSource;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * 分发所有的
     */
    public void dispatchAll() throws Exception {
        if (dataSource == null) {
            throw new IllegalStateException("No data source");
        }
        List accounts = dataSource.getEntities();
        dispatch(accounts);
    }

    /**
     * 同步一组帐户
     *
     * @param items 帐户列表
     */
    public void dispatch(List<A> items) {
        int size = items.size();
        if (size == 0) {
            return;
        }

        int taskCount = size < batchItemSize * batchTaskCount ?
                        (int)Math.ceil((double)size / batchItemSize) :
                        batchTaskCount;
        final BatchStatusCallbackHandler[] handlers = new BatchStatusCallbackHandler[taskCount];
        int position = 0;
        while (position < items.size()) {
            synchronized (handlers) {
                for (int i = 0; i < taskCount; i++) {
                    if (handlers[i] == null || handlers[i].isDone()) {
                        if (position >= items.size()) {
                            break;
                        }
                        position = dispatch(handlers, i, position, items);
                    }
                }
                try {
                    // 由于是异步派发的事件，需要保证回调函数在被调用之前不会失效
                    handlers.wait();
                }
                catch (InterruptedException e) {
                }
            }
        }

        // 全部账户发送完成之后，最后发送出去的账户的回调函数可能还没有
        // 被调用，需要等待它们结束
        synchronized (handlers) {
            boolean allDone = false;

            while (!allDone) {
                allDone = true;
                for (int i = 0; i < handlers.length; i++) {
                    if (handlers[i] == null) {
                    }
                    else if (handlers[i].isDone()) {
                        handlers[i] = null;
                    }
                    else {
                        allDone = false;
                    }
                }
                if (!allDone) {
                    try {
                        handlers.wait();
                    }
                    catch (InterruptedException e) {
                    }
                }
                else {
                    break;
                }
            }
        }
    }

    /**
     * 向服务器派发BatchSyncTask
     *
     * @param handlers
     * @param index
     * @param position
     * @param accounts
     * @return
     */
    private int dispatch(BatchStatusCallbackHandler[] handlers, int index, int position, List<A> accounts) {
        int serverCount = getServerCount();
        if (serverCount == 0) {
            throw new IllegalStateException("No such type of server found:" + uri);
        }

        if (handlers[index] == null) {
            handlers[index] = new BatchStatusCallbackHandler(handlers, timeout);
            handlers[index].setTotalCount(serverCount);
        }
        else {
            handlers[index].reset(serverCount);
        }

        int size;
        int realCount = 0;
        for (int i = 0; i < serverCount; i++) {
            size = accounts.size() - position;
            size = size >= batchItemSize ? batchItemSize : size;
            List<A> itemList = new ArrayList<A>(size);
            copy(accounts, position, itemList, 0, size);
            if (dispatch(itemList, handlers[index])) {
                realCount++;
            }
            position += size;
            if (position >= accounts.size()) {
                break;
            }
        }
        handlers[index].setTotalCount(realCount);
        return position;
    }

    protected abstract boolean dispatch(List<A> items, BatchStatusCallbackHandler handler);

    private void copy(List<A> src, int srcPos, List<A> dest, int destPos, int size) {
        int end = srcPos + size;
        for (int i = srcPos; i < end; i++) {
            dest.add(destPos++, src.get(i));
        }
    }

    /**
     * 计算当前能够处理任务的服务器数量
     *
     * @return 当前能够处理任务的服务器数量
     */
    protected int getServerCount() {
        //根据当前活着的所有这种类型的服务器数量来决定需要产生多少个任务
        String uri = getUri();
        String syncServer = uri.substring(1, uri.indexOf("/"));
        return ClientUtil.getClientCount(syncServer);
    }

    /**
     * 返回批处理帐户大小
     *
     * @return 批处理帐户大小
     */
    public int getBatchItemSize() {
        return batchItemSize;
    }

    /**
     * 设置批处理帐户大小
     *
     * @param batchItemSize 批处理帐户大小
     */
    public void setBatchItemSize(int batchItemSize) {
        if (batchItemSize <= 0 || batchItemSize > 10000) {
            throw new IllegalArgumentException("Illegal batch size:" + batchItemSize);
        }
        this.batchItemSize = batchItemSize;
    }

    // 一个server所执行的BatchSyncTask的数目
    public int getBatchTaskCount() {
        return batchTaskCount;
    }

    public void setBatchTaskCount(int batchTaskCount) {
        this.batchTaskCount = batchTaskCount;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public BatchDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(BatchDataSource dataSource) {
        this.dataSource = dataSource;
    }
}
