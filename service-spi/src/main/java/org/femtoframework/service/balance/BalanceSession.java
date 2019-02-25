package org.femtoframework.service.balance;

import org.femtoframework.service.Session;
import org.femtoframework.service.SessionID;

/**
 * 负载均衡的会话
 *
 * @author fengyun
 * @version 1.00 2005-11-17 10:13:03
 */
public interface BalanceSession extends Session
{
    /**
     * 返回第一个会话，这个方法用于当没有声明相应的服务器类型的时候调用
     *
     * @return 第一个服务器会话
     */
    SessionID getFirstSessionID();

    /**
     * 根据服务器类型找出对应的SessionID
     *
     * @param serverType 服务器类型
     * @return ApsisSessionID
     */
    SessionID getSessionID(String serverType);

    /**
     * 添加SessionID和服务器类型的mapping
     *
     * @param serverType 服务器类型
     * @param sessionId  会话标识
     */
    void setSessionID(String serverType, SessionID sessionId);

    /**
     * 删除相应服务器类型的会话
     *
     * @param serverType 服务器类型
     * @return 相应的会话标示
     */
    SessionID clearSessionID(String serverType);
}
