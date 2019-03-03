package org.femtoframework.service.event;


import org.femtoframework.service.EventValve;

import java.util.List;

/**
 * Event Pipeline
 *
 * @author fengyun
 * @version 1.00 2005-9-19 21:17:58
 */
public interface EventPipeline
{
    /**
     * 返回基础过滤器
     *
     * @return 基础过滤器
     */
    EventValve getBasic();

    /**
     * 设置基础过滤器
     *
     * @param valve 基础过滤器
     */
    void setBasic(EventValve valve);

    /**
     * 添加一个过滤器
     *
     * @param valve 过滤器
     */
    void addValve(EventValve valve);


    /**
     * 返回过滤器数组
     *
     * @return 过滤器数组
     */
    List<EventValve> getValves();


    /**
     * 删除给定的过滤器
     *
     * @param valve 删除过滤器
     */
    void removeValve(EventValve valve);

    /**
     * 处理事件
     *
     * @param event Event
     */
    <E> void handle(E event) throws Exception;
}
