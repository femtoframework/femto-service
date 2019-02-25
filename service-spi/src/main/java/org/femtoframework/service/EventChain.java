package org.femtoframework.service;

/**
 * 事件链
 *
 * @author fengyun
 * @version 1.00 2005-9-19 20:26:07
 */
public interface EventChain
{
    /**
     * 传递给下一个阀门进行处理
     *
     * @param event Event
     */
    void handleNext(Object event) throws Exception;
}
