package org.femtoframework.service.apsis;

import org.femtoframework.service.SessionID;

/**
 * 定位会话标识的工具类
 *
 * @author fengyun
 * @version 1.00 2006-7-15 16:31:07
 */
public class SessionLocal
{
    /**
     * 会话标识存放
     */
    private static ThreadLocal<SessionID> sessionIdLocal = new ThreadLocal<SessionID>();

    /**
     * 设置会话标识
     *
     * @param sessionId 会话标识
     */
    public static void setSessionID(SessionID sessionId)
    {
        sessionIdLocal.set(sessionId);
    }

    /**
     * 设置会话标识
     *
     * @param sessionId 会话标识
     */
    public static void setSessionID(String sessionId)
    {
        if (sessionId == null) {
            sessionIdLocal.set(null);
        }
        else {
            sessionIdLocal.set(new SimpleSessionID(sessionId));
        }
    }

    /**
     * 返回会话标识
     *
     * @return 会话标识
     */
    public static SessionID getSessionID()
    {
        return sessionIdLocal.get();
    }
}
