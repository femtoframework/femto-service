package org.femtoframework.service.apsis;

import org.femtoframework.service.SessionID;

/**
 * Apsis会话标识
 *
 * @author fengyun
 * @version 1.00 2005-8-6 15:27:49
 */
public interface ApsisSessionID extends SessionID
{
    /**
     * 返回服务器标识
     *
     * @return 服务器标识
     */
    long getServerId();

    /**
     * 内部区别于同类型的对象的唯一标识
     *
     * @return 唯一标识
     */
    int getIdentity();

    /**
     * 返回扩展标识
     *
     * @return 扩展标识
     */
    int getExtension();
}
