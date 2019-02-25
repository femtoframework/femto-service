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
     * 默认的会话范围
     */
    Scope DEFAULT_SESSION_SCOPE = Session.SCOPE_SERVICE;

    /**
     * 创建当前用户Session，它与getSession(true)不同的是它不把创建的会话保存为自身使用的会话
     *
     * @param scope 范围 Scope.NAMESPACE 或者 Scope.SERVER
     * @return [Session] 创建的会话
     */
    Session createSession(Scope scope);

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
     * 返回当前用户Session
     *
     * @param scope 范围 Scope.NAMESPACE 或者 Scope.SERVER
     * @return [Session] 当前用户会话
     * @see Session
     */
    Session getSession(Scope scope);

    /**
     * 返回当前用户Session
     *
     * @param scope  范围 Scope.NAMESPACE 或者 Scope.SERVER
     * @param create 如果<code>true</code>，当<code>session==null</code>时建会话
     * @return [Session] 当前用户会话
     * @see Session
     */
    Session getSession(Scope scope, boolean create);

    /**
     * 结束当前用户在当前服务中的Session
     *
     * @see Session
     */
    void endSession();

    /**
     * 结束当前会话
     *
     * @param scope 范围 Scope.NAMESPACE 或者 Scope.SERVER
     * @see Session
     */
    void endSession(Scope scope);
}
