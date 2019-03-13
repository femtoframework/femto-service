package org.femtoframework.service.apsis.session;

import org.femtoframework.parameters.Parameters;
import org.femtoframework.service.ServerID;
import org.femtoframework.service.Session;
import org.femtoframework.service.SessionID;

/**
 * 会话产生器
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-7-26 16:38:45
 */
public interface SessionGenerator<S extends Session>
{
    /**
     * 设置服务标识
     *
     * @param serverId 服务标识
     */
    void setServerID(ServerID serverId);

    /**
     * 根据环境变量产生会话
     *
     * @param env 环境变量
     */
    S generate(Parameters env);

    /**
     * 根据会话标识和环境变量产生会话
     *
     * @param sessionId 会话标识，如果为<code>null<code>则调用<br>
     *                  #generateID(Parameters)产生标识<br>
     * @param env       环境变量
     */
    S generate(SessionID sessionId, Parameters env);

    /**
     * 产生会话标识
     *
     * @param env 参数
     */
    SessionID generateID(Parameters env);

    /**
     * 回收会话
     *
     * @param session 会话
     */
    void recycle(S session);
}

