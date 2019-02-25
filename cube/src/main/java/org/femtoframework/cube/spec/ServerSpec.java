package org.femtoframework.cube.spec;

import java.util.List;

/**
 * 服务器定义
 *
 * @author fengyun
 * @version 1.00 2005-2-5 11:42:25
 */
public interface ServerSpec
{
    /**
     * 返回服务器类型
     *
     * @return 服务器类型
     */
    String getType();

    /**
     * 返回服务端口
     *
     * @return 服务端口
     */
    int getPort();

    /**
     * 返回所有连接
     *
     * @return 所有的连接
     */
    List<ConnectionSpec> getConnections();
}
