package org.femtoframework.service.event;

/**
 * 事件侦听器
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-12-19 15:12:09
 */
public interface EventCallbackHandler
{
    /**
     * 当事件执行完成时触发
     *
     * @param callback
     */
    void callback(EventCallback callback);
}
