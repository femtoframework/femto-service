package org.femtoframework.service.client;

import org.femtoframework.parameters.Parameters;
import org.femtoframework.service.Client;
import org.femtoframework.util.selector.ListSelector;

import java.util.List;

/**
 * 客户端列表
 *
 * @author fengyun
 * @version 1.00 May 4, 2004 4:25:18 PM
 */
public interface ClientList<C extends Client>
{
    /**
     * 根据索引返回对应的客户端
     *
     * @param i 偏址
     * @return 对应的客户端
     */
    C getClient(int i);

    /**
     * 添加客户端
     *
     * @param client 客户端
     */
    void addClient(C client);

    /**
     * 删除客户端
     *
     * @param client 客户端
     * @return 客户端
     */
    boolean removeClient(C client);

    /**
     * 删除客户端
     *
     * @param id
     * @return 客户端
     */
    boolean removeClient(long id);

    /**
     * 判断是否拥有指定的客户端
     *
     * @param client 客户端
     * @return 是否拥有指定的客户端
     */
    boolean containsClient(C client);

    /**
     * 选取其中一个客户端
     *
     * @return 选取下一台
     */
    C findClient();

    /**
     * 选取提供给定服务的客户端
     *
     * @param selector 选取器
     * @param request  请求
     */
    C findClient(ListSelector selector, Parameters request);

    /**
     * 返回当前列表中的客户端数目
     *
     * @return 客户端数目
     */
    int getCount();

    /**
     * 获取所有的客户端
     *
     * @return 获取所有的客户端
     */
    List<C> getAll();

}
