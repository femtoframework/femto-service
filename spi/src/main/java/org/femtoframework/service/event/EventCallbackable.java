package org.femtoframework.service.event;

import javax.naming.Name;

/**
 * EventCallbackable
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2006-4-13 18:38:23
 */
public interface EventCallbackable {
    /**
     * 获取回掉
     *
     * @return
     */
    Name getCallbackHandlerName();

    /**
     * 设置回掉
     *
     * @param callbackHandlerName CallbackHandler Name
     */
    void setCallbackHandlerName(Name callbackHandlerName);

    /**
     * Return the Event Callback Handler
     *
     * @return
     */
    EventCallbackHandler getCallbackHandler();

    /**
     * Overwrite Callback Handler
     *
     * @param callbackHandler Callback Handler
     */
    void _setCallbackHandler(EventCallbackHandler callbackHandler);
}
