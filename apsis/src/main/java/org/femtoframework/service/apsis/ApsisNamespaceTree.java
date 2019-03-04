package org.femtoframework.service.apsis;

import org.femtoframework.parameters.Parameters;
import org.femtoframework.service.client.ClientList;
import org.femtoframework.util.selector.ListSelector;

/**
 * 服务树，根据服务来对服务器进行归类
 *
 * @author fengyun
 * @version 1.00 2005-11-18 16:44:38
 */
public interface ApsisNamespaceTree
{
    /**
     * 返回拥有某一类型服务的客户端列表
     *
     * @param serviceName 服务名称
     */
    ClientList<ApsisClient> getClientList(String serviceName);

    /**
     * 根据服务器标识删除其相关的所有服务信息
     *
     * @param serverId 服务器标识
     */
    void removeNamespaces(long serverId);

    /**
     * 删除给定客户端多提供的所有服务
     *
     * @param client 客户端
     */
    void removeClient(ApsisClient client);

    /**
     * 选取提供给定服务的客户端
     *
     * @param name 服务名称
     */
    ApsisClient findClient(String name);

    /**
     * 选取提供给定服务的客户端
     *
     * @param name       服务名称
     * @param selector   选取器
     * @param parameters 请求
     */
    ApsisClient findClient(String name, ListSelector selector, Parameters parameters);

    /**
     * 添加服务器Metadata信息，根据Metadata信息更新服务树上的客户端映射
     *
     * @param metadata 服务器Metadata信息
     */
    void addServerMetadata(ServerMetadata metadata);
}
