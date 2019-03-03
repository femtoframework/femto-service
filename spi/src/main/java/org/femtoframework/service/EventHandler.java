package org.femtoframework.service;

import java.util.EventListener;

/**
 * 事件处理器
 *
 * @author fengyun
 * @version 1.00 2005-9-19 22:37:08
 */
public interface EventHandler extends EventListener
{
    /**
     * 处理事件
     *
     * @param event Event
     */
    void handle(Event event) throws Exception;
}
