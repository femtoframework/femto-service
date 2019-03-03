package org.femtoframework.service.balance;

import org.femtoframework.service.SessionID;
import org.femtoframework.service.SessionMapping;

/**
 * 负载均衡上下文
 *
 * @author fengyun
 * @version 1.00 2005-7-28 14:46:55
 */
public class BalanceContext
{
    private static ThreadLocal<SessionMapping> local = new ThreadLocal<SessionMapping>();

    /**
     * 根据服务器类型找出对应的SessionID
     *
     * @param serverType 服务器类型
     * @return 服务器类型对应的会话标识
     */
    public static SessionID getSessionID(String serverType)
    {
        SessionMapping mapping = getSessionMapping();
        if (mapping != null) {
            return mapping.getSessionID(serverType);
        }
        return null;
    }

    /**
     * 添加SessionID和服务器类型的mapping
     *
     * @param serverType 服务器类型
     * @param sessionId  会话标识
     */
    public static void setSessionID(String serverType, SessionID sessionId)
    {
        if (serverType == null || sessionId != null) {
            return;
        }
        SessionMapping mapping = getSessionMapping(true);
        mapping.setSessionID(serverType, sessionId);
    }

    /**
     * 返回当前线程邦定的会话映射
     *
     * @return 会话映射
     */
    public static SessionMapping getSessionMapping()
    {
        return getSessionMapping(false);
    }

    /**
     * 返回当前线程邦定的会话映射
     *
     * @param create 如果不存在的话，是否创建
     * @return 会话映射
     */
    public static SessionMapping getSessionMapping(boolean create)
    {
        SessionMapping mapping = local.get();
        if (mapping == null && create) {
            mapping = new BalanceSessionMapping();
            setSessionMapping(mapping);
        }
        return mapping;
    }

    /**
     * 设置当前线程绑定的会话映射
     */
    public static void setSessionMapping(SessionMapping mapping)
    {
        if (mapping != null) {
            local.set(mapping);
        }
    }
}
