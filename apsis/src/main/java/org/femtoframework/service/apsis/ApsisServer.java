package org.femtoframework.service.apsis;

import org.femtoframework.service.Connector;
import org.femtoframework.service.Server;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Apsis服务器
 * <p/>
 * 包括服务器管理、容器管理、连接管理和线程池等
 *
 * @author fengyun
 * @version 1.00 2005-5-22 15:56:56
 */
public interface ApsisServer extends Server
{
    /**
     * 添加服务器
     *
     * @param server 服务器
     */
    void addServer(Server server);

    /**
     * 删除服务器
     *
     * @param name 服务器名称
     * @return server
     */
    Server removeServer(String name);

    /**
     * 根据服务器名称返回服务器
     *
     * @param name 服务器名称
     * @return server
     */
    Server getServer(String name);

    /**
     * 返回所有服务器名称
     *
     * @return 所有服务器名称
     */
    Collection<String> getServerNames();

    /**
     * 设置线程执行器
     *
     * @param executor 线程池
     */
    void setExecutor(ExecutorService executor);

    /**
     * 返回线程执行器
     *
     * @return 线程执行器
     */
    ExecutorService getExecutor();


    /**
     * 添加连接器
     *
     * @param connector 连接器
     */
    void addConnector(Connector connector);

    /**
     * 返回连接器
     *
     * @param name 连接器名称
     * @return 连接器
     */
    Connector getConnector(String name);

    /**
     * 根据连接器名称删除连接器
     *
     * @param name 连接器名称
     * @return 连接器
     */
    Connector removeConnector(String name);

    /**
     * 返回所有连接器名称
     *
     * @return 所有连接器名称
     */
    Collection<String> getConnectorNames();

    /**
     * 返回服务器应用类型
     *
     * @return 服务器应用类型
     */
    String getType();

    /**
     * 返回服务器标识
     *
     * @return 服务器标识
     */
    long getId();

    /**
     * 处理消息
     *
     * @param message 消息
     */
    void process(Object message);
}
