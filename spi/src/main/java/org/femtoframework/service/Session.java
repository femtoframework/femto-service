package org.femtoframework.service;


import org.femtoframework.parameters.Parameters;

import javax.security.auth.Subject;
import java.io.Serializable;

/**
 * 用户会话
 * <p/>
 * 是用户在服务器上的有效信息保存类
 *
 * @see Parameters
 */
public interface Session extends Parameters, Serializable
{
    /**
     * 返回会话标识
     */
    SessionID getSessionID();

    /**
     * 返回字符串形式的会话标识
     */
    String getId();

    /**
     * 返回会话创建时间
     *
     * @return a <code>long</code> specifying
     *         when this session was created,
     *         expressed in
     *         milliseconds since 1/1/1970 GMT
     */
    long getStartingTime();

    /**
     * 返回最后访问时间
     *
     * @return 最后访问时间 a <code>long</code>
     *         representing the last time
     *         the client sent a request associated
     *         with this session, expressed in
     *         milliseconds since 1/1/1970 GMT
     */
    long getLastAccessedTime();

    /**
     * 返回有效时间（单位：毫秒）
     *
     * @return 有效时间
     */
    int getTimeout();

    /**
     * 设置超时时间
     *
     * @param timeout 有效时间
     */
    void setTimeout(int timeout);

    /**
     * 判断会话是否超时
     *
     * @return 超时返回<code>true</code>，否则返回<code>false</code>
     */
    boolean isTimeout();

    /**
     * 结束会话（执行之后会话从池中删除）
     */
    void expire();

    /**
     * 更新最后访问时间
     */
    void access();

    /**
     * 返回当前会话对应的Subject
     *
     * @return 当前的Subject
     */
    Subject getSubject();

    /**
     * 设置认证主题
     *
     * @param subject 认证主题
     */
    void setSubject(Subject subject);
}
