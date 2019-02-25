package org.femtoframework.service;



/**
 * 会话标识
 *
 * @author fengyun
 * @version 1.00 2005-6-12 23:14:52
 */
public interface SessionID
{
    /**
     * 返回哈希码
     *
     * @return 哈希码
     */
    int hashCode();

    /**
     * 返回对象是否与标识等效
     *
     * @return 如果等效返回<code>true</code> 反之则返回<code>false</code>
     */
    boolean equals(Object obj);

    /**
     * 返回字符串标识
     *
     * @return 字符串标识
     */
    String toString();
}
