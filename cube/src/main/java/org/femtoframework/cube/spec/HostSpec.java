package org.femtoframework.cube.spec;

import java.util.List;

/**
 * 系统中的一台主机
 *
 * @author fengyun
 * @version 1.00 2005-2-5 11:41:08
 */
public interface HostSpec
{
    /**
     * 返回主机名
     *
     * @return 主机名
     */
    String getName();

    /**
     * 返回地址
     *
     * @return 返回首选IP地址
     */
    String getAddress();

    /**
     * 返回主机地址，主机可用的IP地址，这个方法适合于一个主机在两个网段内，需要在不同网段内的服务进行通讯.
     * 多个IP地址的第一个作为服务器的唯一标识。
     *
     * @return 主机地址
     */
    String[] getAddresses();

    /**
     * 返回所有服务器
     *
     * @return 所有服务器
     */
    List<String> getServers();
}
