package org.femtoframework.cube;

import java.util.Collection;

/**
 * 应用服务器接口，这里只定义Endpoint和Server的概念，但不具体限定某一特殊的接口
 * 主要是为了程序的灵活性和减少对CUBE本身的依赖。
 * 这里只根据名称来访问具体的对象。
 *
 * @author fengyun
 * @version 1.00 2005-3-10 1:07:02
 */
public interface AppServer
{
    /**
     * 返回所有的接入点的名称
     */
    Collection<String> getEndpointNames();

    /**
     * 根据名称返回接入点
     *
     * @param name 名称
     * @return 如果存在返回相应的接入点，否则返回<code>null</code>
     */
    Endpoint getEndpoint(String name);

    /**
     * 添加接入点
     *
     * @param endpoint 接入点
     */
    void addEndpoint(Endpoint endpoint);

    /**
     * 返回所有的服务器的名称
     */
    Collection<String> getServerNames();

    /**
     * 根据名称返回服务器
     *
     * @param name 名称
     * @return 如果存在返回相应的服务器，否则返回<code>null</code>
     */
    Object getServer(String name);

    /**
     * 添加服务器
     *
     * @param name   服务器名称
     * @param server 服务器
     */
    void addServer(String name, Object server);
}
