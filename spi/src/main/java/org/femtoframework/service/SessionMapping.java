package org.femtoframework.service;

/**
 * 会话管理，管理不同服务器的会话标识
 *
 * @author fengyun
 * @author <a href="mailto:yqchen@naesasoft.com">yqchen</a>
 * @version 1.1 2005-7-27 11:25:25
 *          1.00 2005-7-12 16:58:17
 */
public interface SessionMapping
{
    /**
     * 会话映射常量定义（主要是固定在某一变量空间中的名称）
     */
    String SESSION_MAPPING = "session_mapping";

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
}
