package org.femtoframework.service;

import java.util.EventListener;

/**
 * 会话侦听者
 *
 * @author fengyun
 * @version 1.00 Jun 10, 2002 3:31:05 PM
 */
public interface SessionListener
    extends EventListener
{
    /**
     * 通知桢听者，会话被创建
     *
     * @param se the notification event
     */
    void created(SessionEvent se);

    /**
     * 通知桢听者会话被销毁
     *
     * @param se the notification event
     */
    void destroyed(SessionEvent se);
}
