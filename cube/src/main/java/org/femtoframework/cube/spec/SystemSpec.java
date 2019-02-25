package org.femtoframework.cube.spec;


import org.femtoframework.bean.NamedBean;

import java.util.List;


/**
 * 系统定义
 *
 * @author fengyun
 * @version 1.00 2005-3-1 16:29:01
 */
public interface SystemSpec extends NamedBean {

    /**
     * Returns the Platform
     *
     * @return Platform
     */
    Platform getPlatform();

    /**
     * Current Cube Version
     *
     * @return Cube Version
     */
    String getVersion();

    /**
     * 返回系统标示符，为了能够标识在一个网络内的不同系统，而产生的一个标识。
     * 如果系统没有设置名称，那么返回的是这个标识
     *
     * @return 系统标识
     */
    String getId();

    /**
     * 返回所有的主机
     *
     * @return 所有的主机
     */
    List<HostSpec> getHosts();


    /**
     * 返回所有拥有相应服务器类型的主机
     *
     * @return 所有的主机
     */
    List<HostSpec> getHostsByServer(String server);

    /**
     * 根据主机名返回主机
     *
     * @param name 主机名
     * @return 主机
     */
    HostSpec getHost(String name);

    /**
     * 返回所有的服务器定义
     *
     * @return 所有的服务器定义
     */
    List<ServerSpec> getServers();

    /**
     * 根据服务器类型返回服务器定义
     *
     * @param type 服务器类型
     * @return 服务器定义
     */
    ServerSpec getServer(String type);

    /**
     * 返回当前服务器定义
     *
     * @return 服务器定义
     */
    ServerSpec getCurrentServer();

    /**
     * 返回当前主机定义
     *
     * @return 主机定义
     */
    HostSpec getCurrentHost();
}
