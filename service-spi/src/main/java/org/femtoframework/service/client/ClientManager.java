package org.femtoframework.service.client;

import org.femtoframework.service.Client;

import java.net.URI;
import java.util.Collection;
import java.util.List;

/**
 * 客户端映射
 *
 * @author fengyun
 * @version 1.00 May 4, 2004 4:27:48 PM
 */
public interface ClientManager<C extends Client> {

    /**
     * Get Balancer
     *
     * @return Balancer
     */
    Balancer<C> getBalancer();

    /**
     * 添加客户端
     *
     * @param client
     */
    void addClient(C client);

    /**
     * 是否拥有以指定标识的客户端
     *
     * @param id 客户端标识
     */
    boolean hasClient(long id);

    /**
     * 删除指定的客户端
     *
     * @param client Client
     */
    void removeClient(C client);

    /**
     * 返回所有的客户端
     *
     * @return 所有的客户端
     */
    List<C> getClients();

    /**
     * 返回所有客户端的名称
     *
     * @return 所有的客户端标识
     */
    Collection<Long> getIds();

    /**
     * 返回SocketClient数目，包括未连接的
     */
    int getCount();

    /**
     * 根据客户端标识返回客户端
     *
     * @param id 客户端标识
     */
    C getClient(long id);

    /**
     * 根据主机地址和端口返回客户端
     *
     * @param host 主机
     * @param port 端口
     */
    C getClient(String host, int port);

    /**
     * 根据主机地址和端口返回客户端
     *
     * @param host 主机
     * @param port 端口
     */
    C getClient(String host, int port, boolean create);

    /**
     * @param id
     * @param create
     */
    C getClient(long id, boolean create);

    /**
     * 根据应用类型返回所有的客户端
     *
     * @param appType 应用类型
     */
    ClientList<C> getClients(String appType);

    /**
     * 获取指定类型服务器的数量
     * 指已经连接到的，如果连接失败将不计数
     *
     * @param appType 应用类型
     * @return 数量
     */
    int getClientCount(String appType);

    /**
     * 创建客户端
     *
     * @param uri URI地址
     * @return 客户端对象仅仅执行到配置的步骤，不调用Lifecycle相关的方法
     */
    C createClient(URI uri);

}
