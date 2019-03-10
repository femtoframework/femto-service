package org.femtoframework.service;

/**
 * 处理Session的抽象接口
 *
 * @author fengyun
 * @version 1.00 Mar 6, 2002 6:27:22 PM
 * @see Request
 */
public interface SessionContext
{
    /**
     * 创建当前用户在当前服务中的Session，它与getSession(true)不同的是它不把创建的会话保存为自身使用的会话
     *
     * @return [Session] 创建的会话
     */
    Session createSession();

    /**
     * 返回当前用户在当前服务中的Session
     *
     * @return [Session] 当前用户会话
     * @see Session
     */
    Session getSession();

    /**
     * 返回当前用户在当前服务中的Session
     *
     * @param create 如果<code>true</code>，当<code>session==null</code>时建会话
     * @return [Session] 当前用户会话
     * @see Session
     */
    Session getSession(boolean create);

    /**
     * 结束当前用户在当前服务中的Session
     *
     * @see Session
     */
    void endSession();
}
