package org.femtoframework.service.balance;

import org.femtoframework.service.SessionID;
import org.femtoframework.service.SessionMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * 负载均衡会话映射
 *
 * @author fengyun
 * @version 1.00 2005-7-28 15:00:24
 */
public class BalanceSessionMapping implements SessionMapping
{
    private Map<String, SessionID> map = new HashMap<String, SessionID>(8);

    /**
     * 根据服务器类型找出对应的SessionID
     *
     * @param serverType 服务器类型
     * @return 会话标识
     */
    public SessionID getSessionID(String serverType)
    {
        return map.get(serverType);
    }

    /**
     * 添加SessionID和服务器类型的mapping
     *
     * @param serverType 服务器类型
     * @param sessionId  会话标识
     */
    public void setSessionID(String serverType, SessionID sessionId)
    {
        map.put(serverType, sessionId);
    }
}
