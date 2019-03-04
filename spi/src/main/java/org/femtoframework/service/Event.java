package org.femtoframework.service;


import org.femtoframework.service.event.EventCallbackable;

/**
 * 事件，
 * <p/>
 * 自己本身拥有callback功能
 *
 * @author fengyun
 * @author rEneX
 * @version 1.00 Sep 10, 2005 4:11:52 AM
 */
public interface Event extends Arguments, SessionContext, EventCallbackable
{
    /**
     * 返回事件发送者主机地址
     *
     * @return 远程主机地址
     */
    String getRemoteHost();

    /**
     * 返回事件发送者主机端口
     *
     * @return 远程端口
     */
    int getRemotePort();

    /**
     * 返回当前用户Session
     *
     * @param create 如果<code>true</code>，当<code>session==null</code>时建会话
     * @param check  是否检查Session是否超时
     * @return [Session] 当前用户会话
     */
    Session getSession(boolean create, boolean check)
        throws SessionTimeoutException;
}
