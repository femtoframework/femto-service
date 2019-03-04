package org.femtoframework.service;

import javax.security.auth.Subject;

/**
 * 抽象请求接口
 *
 * @author fengyun
 * @version 1.1 2005-4-7 11:51:04
 */
public interface Request extends Arguments, SessionContext
{
    /**
     * 返回请求发送者主机地址
     *
     * @return 远程主机地址
     */
    String getRemoteHost();

    /**
     * 返回请求发送者主机端口
     *
     * @return 远程端口
     */
    int getRemotePort();

    /**
     * 返回当前请求对应的Subject，如果请求中没有，那么检查相应的Session中是否存在
     *
     * @return 当前的Subject
     */
    Subject getSubject();

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
