package org.femtoframework.service;

/**
 * 连接需要主机地址和端口的信息
 *
 * @author fengyun
 * @version 1.00 2005-5-8 10:26:20
 */
public interface AddressAware
{
    /**
     * 设置主机地址
     *
     * @param host 主机地址
     */
    void setHost(String host);

    /**
     * 设置端口
     *
     * @param port 端口
     */
    void setPort(int port);
}
