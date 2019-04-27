package org.femtoframework.service.event;

import java.util.EventListener;

/**
 * 通用事件处理器
 *
 * @author fengyun
 * @version 1.00 2005-9-20 10:03:16
 */
public interface GenericEventHandler extends EventListener
{
    /**
     * 处理事件对象
     *
     * @param event 事件对象
     */
    void handle(GenericEvent event) throws Exception;
}
