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
    private Scope scope = null;

    /**
     * 构造
     *
     * @param source 会话
     * @param scope  会话空间
     */
    public SessionEvent(Session source, Scope scope)
    {
        super(source);
        this.scope = scope;
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

    /**
     * 获取空间设置
     *
     * @return 获取空间设置
     */
    public Scope getScope()
    {
        return scope;
    }


    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("SessionEvent");
        sb.append("{scope=").append(scope);
        sb.append(",source=").append(source);
        sb.append('}');
        return sb.toString();
    }
}
