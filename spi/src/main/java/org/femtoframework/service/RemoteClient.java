package org.femtoframework.service;

/**
 * 远程客户端标识接口
 *
 * @author fengyun
 * @version 1.00 May 10, 2004 2:32:07 PM
 */
public interface RemoteClient
    extends Client
{
    /**
     * 判断远程服务器是否正常
     *
     * @return 是否活动状态
     */
    boolean isAlive();

    /**
     * 是否长连接
     *
     * @return
     */
    boolean isLongTerm();
}
