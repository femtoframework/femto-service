package org.femtoframework.service.apsis.session;

import org.femtoframework.service.Container;
import org.femtoframework.service.Session;
import org.femtoframework.service.SessionID;
import org.femtoframework.service.SessionListener;

import java.util.Collection;

/**
 * 会话容器
 *
 * @author fengyun
 * @version 1.00 2005-6-17 0:43:28
 */
public interface SessionContainer<S extends Session> extends Container
{
    /**
     * 返回会话的数目
     *
     * @return 数目
     */
    int getCount();

    /**
     * 返回会话有效期
     *
     * @return 有效期
     */
    int getTimeout();

    /**
     * 返回会话
     *
     * @param sessionId 会话标识
     * @return 会话，如果会话已经超时或者不存在返回<code>null</code>
     */
    S getSession(SessionID sessionId);

    /**
     * 获取所有的session，返回一个只读的集合
     *
     * @return sessions
     */
    Collection<S> getSessions();

    /**
     * 添加会话
     *
     * @param session 会话
     */
    void addSession(S session);

    /**
     * 删除会话
     *
     * @param sessionId 会话标识
     */
    void removeSession(SessionID sessionId);

    /**
     * 设置会话有效期
     *
     * @param timeout 有效期
     */
    void setTimeout(int timeout);

    /**
     * 添加Session侦听者
     *
     * @param listener Session侦听者
     */
    void addSessionListener(SessionListener listener);

    /**
     * 删除Session侦听者
     *
     * @param listener Session侦听者
     */
    void removeSessionListener(SessionListener listener);
}
