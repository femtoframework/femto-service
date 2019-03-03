package org.femtoframework.service;

import java.util.EventListener;

/**
 * ComponentListener
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2006-3-8 15:13:00
 */
public interface ComponentListener
    extends EventListener
{
    /**
     * 触发事件
     *
     * @param event 事件
     */
    void handle(ComponentEvent event);
}
