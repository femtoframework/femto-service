package org.femtoframework.cube.spec;

/**
 * 连接接口
 *
 * @author fengyun
 * @version 1.00 2005-2-5 11:48:24
 */
public interface ConnectionSpec
{
    /**
     * 返回目标主机模式
     *
     * @return 目标主机模式
     */
    String getHost();

    /**
     * 返回目标服务器模式
     *
     * @return 目标服务器模式
     */
    String getBackend();

    /**
     * 返回连接数目
     *
     * @return 连接数目
     */
    int getCount();
}
