package org.femtoframework.service;

/**
 * 需要会话上下文的对象
 *
 * @author fengyun
 * @version 1.00 2005-7-6 1:07:31
 */
public interface SessionContextAware
{
    /**
     * 设置会话上下文
     *
     * @param sessionContext 会话上下文
     */
    void setSessionContext(SessionContext sessionContext);
}
