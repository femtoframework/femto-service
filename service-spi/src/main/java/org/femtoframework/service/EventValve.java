package org.femtoframework.service;

/**
 * 事件阀门，对事件的桢听进行过滤
 *
 * @author fengyun
 * @version 1.00 2005-9-19 20:25:19
 */
public interface EventValve
{
    /**
     * 处理事件
     *
     * @param event Event
     * @param chain 过滤器链
     */
    <E> void handle(E event, EventChain chain) throws Exception;
}
