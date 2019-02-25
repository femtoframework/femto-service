package org.femtoframework.service;

/**
 * 客户端
 *
 * @author fengyun
 * @version 1.00 2005-5-22 1:19:13
 */
public interface Client {
    /**
     * 返回对应的服务器类型，对应于一项应用：mail admin ...
     *
     * @return 对应的服务器类型
     */
    String getServerType();

    /**
     * 返回对应的服务器标识
     *
     * @return 对应的服务器标识
     */
    long getId();
}
