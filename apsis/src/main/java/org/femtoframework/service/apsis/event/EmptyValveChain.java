package org.femtoframework.service.apsis.event;


import org.femtoframework.service.EventChain;

/**
 * Empty Valve Chain
 *
 * @author fengyun
 * @version 1.00 2005-9-19 22:17:04
 */
public class EmptyValveChain implements EventChain
{
    private static EmptyValveChain instance = new EmptyValveChain();

    /**
     * 构造
     */
    private EmptyValveChain()
    {
    }

    /**
     * 返回实例
     */
    public static EmptyValveChain getInstance()
    {
        return instance;
    }

    /**
     * 传递给下一个阀门进行处理
     *
     * @param event Event
     */
    public void handleNext(Object event)
    {
    }
}
