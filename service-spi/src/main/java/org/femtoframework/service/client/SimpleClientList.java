package org.femtoframework.service.client;

import org.femtoframework.parameters.Parameters;
import org.femtoframework.service.Client;
import org.femtoframework.util.selector.ListSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单客户端列表
 *
 * @author fengyun
 * @version 1.00 2005-11-18 17:45:34
 */
public class SimpleClientList<C extends Client>
    implements ClientList<C>
{
    /**
     * 客户端列表
     */
    private final List<C> list = new ArrayList<C>();

    /**
     * 选取指针
     */
    private int next = 0;

    /**
     * 根据索引返回对应的客户端
     *
     * @param i 偏址
     * @return 对应的客户端
     */
    public C getClient(int i)
    {
        return list.get(i);
    }

    /**
     * 添加客户端
     *
     * @param client 客户端
     */
    public void addClient(C client)
    {
        if (client == null) {
            return;
        }

        synchronized (list) {
            list.add(client);
        }
    }

    /**
     * 删除客户端
     *
     * @param client 客户端
     * @return 客户端
     */
    public boolean removeClient(C client)
    {
        if (client == null) {
            return false;
        }
        synchronized (list) {
            list.remove(client);
        }
        return true;
    }

    /**
     * 删除客户端
     *
     * @param id 客户端标识
     * @return 客户端
     */
    public boolean removeClient(long id)
    {
        synchronized (list) {
            C client = null;
            for (C aList : list) {
                client = aList;
                if (client.getId() == id) {
                    break;
                }
                client = null;
            }
            if (client != null) {
                list.remove(client);
                return true;
            }
            return false;
        }
    }

    /**
     * 判断是否拥有指定的客户端
     *
     * @param client 客户端
     * @return 是否拥有指定的客户端
     */
    public boolean containsClient(C client)
    {
        return list.contains(client);
    }

    /**
     * 选取其中一个客户端
     *
     * @return 选取下一台
     */
    public C findClient()
    {
        synchronized (list) {
            int size = list.size();
            if (size == 0) {
                return null;
            }
            next %= size;
            return list.get(next++);
        }
    }

    /**
     * 选取提供给定服务的客户端
     *
     * @param selector 选取器
     * @param request  请求
     */
    public C findClient(ListSelector selector, Parameters request)
    {
        return selector.select(list, request);
    }

    /**
     * 返回当前列表中的客户端数目
     *
     * @return 客户端数目
     */
    public int getCount()
    {
        return list.size();
    }

    /**
     * 获取所有的客户端
     *
     * @return 所有的客户端
     */
    public List<C> getAll()
    {
        return list;
    }
    
    public String toString()
    {
    	return list.toString();
    }
}
