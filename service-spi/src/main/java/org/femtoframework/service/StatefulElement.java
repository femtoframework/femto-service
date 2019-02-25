package org.femtoframework.service;

/**
 * 有状态的请求
 *
 * @author fengyun
 * @version 1.00 2005-11-17 10:46:35
 */
public interface StatefulElement
{
    /**
     * 返回Session标识
     *
     * @return [String] Session标识
     */
    SessionID getSessionID();

    /**
     * 设置会话标识(用于找到服务器会话和服务会话）
     *
     * @param sessionId 会话标识，如果是null，表示清除会话标识
     */
    void setSessionID(SessionID sessionId);
}
