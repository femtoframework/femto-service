package org.femtoframework.service.event;

/**
 * EventCallbackable
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2006-4-13 18:38:23
 */
public interface EventCallbackable {
    /**
     * 添加侦听器
     *
     * @param callbackHandler
     */
    void addCallbackHandler(EventCallbackHandler callbackHandler);

    /**
     * 获取回掉
     *
     * @return
     */
    EventCallbackHandler getCallbackHandler();

    /**
     * 设置回掉
     *
     * @param callbackHandler
     */
    void setCallbackHandler(EventCallbackHandler callbackHandler);
}
