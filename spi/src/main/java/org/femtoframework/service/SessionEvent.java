package org.femtoframework.service;

import java.util.EventObject;

/**
 * 会话事件，原先通过SessionListener实现一些自动的初始化工作的功能将不再支持，
 * 因为这样存在多线程安全问题，类似的需求可以通过过滤器或者拦截机来实现
 *
 * @author fengyun
 * @version 1.00 Jun 10, 2002 3:29:24 PM
 */
public class SessionEvent
    extends EventObject
{
    /**
     * 构造
     *
     * @param source 会话
     */
    public SessionEvent(Session source)
    {
        super(source);
    }

    /**
     * 返回会话
     *
     * @return 会话
     */
    public Session getSession()
    {
        return (Session) super.getSource();
    }

    public String toString()
    {
        return "SessionEvent" +
                "{source=" + source +
                '}';
    }
}
