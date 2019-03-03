package org.femtoframework.service.client;

import org.femtoframework.bean.Destroyable;
import org.femtoframework.bean.Stoppable;
import org.femtoframework.service.Client;
import org.femtoframework.service.ServerID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 简单客户端映射
 *
 * @author fengyun
 * @version 1.00 2005-5-22 10:31:37
 */
public abstract class AbstractClientManager<C extends Client> implements ClientManager<C> {
    //ServerID --> Client
    protected Map<Long, C> id2Client;

    /**
     * 所有的客户端列表
     */
    private final List<C> clients;


    //Type --> ArrayList
    private final Map<String, ClientList<C>> type2List;

    /**
     * 构造
     */
    public AbstractClientManager() {
        id2Client = new HashMap<Long, C>();
        clients = new ArrayList<C>();
        type2List = new HashMap<String, ClientList<C>>(8);

        Runtime.getRuntime().addShutdownHook(new Thread(new ClientStopThread()));
    }

    private void addByType(C client) {
        String type = client.getServerType();
        //有可能是组播通讯等客户端
        if (type == null) {
            return;
        }
        ClientList<C> list = type2List.get(type);
        if (list == null) {
            synchronized (type2List) {
                list = type2List.get(type);
                if (list == null) {
                    list = new SimpleClientList<C>();
                    type2List.put(type, list);
                }
            }
        }
        list.addClient(client);
    }

    /**
     * 删除指定的客户端
     *
     * @param client Client
     */
    public void removeClient(C client) {
        C obj = id2Client.remove(client.getId());
        if (obj != null) {
            synchronized (clients) {
                clients.remove(obj);
            }
            removeByType(obj);
        }
    }

    private void removeByType(C client) {
        String type = client.getServerType();
        ClientList<C> list = type2List.get(type);
        if (list != null) {
            synchronized (list) {
                list.removeClient(client);
            }
        }
    }

    /**
     * 根据服务器类型返回所有的客户端
     *
     * @param type 服务器类型
     */
    public ClientList<C> getClients(String type) {
        return type2List.get(type);
    }

    @Override
    public int getClientCount(String appType) {
        ClientList<C> list = getClients(appType);
        return list != null ? list.getCount() : 0;
    }


    /**
     * 添加客户端
     *
     * @param client
     */
    public void addClient(C client) {
        id2Client.put(client.getId(), client);
        synchronized (clients) {
            clients.add(client);
        }
        addByType(client);
    }

    /**
     * 是否拥有以指定标识的客户端
     *
     * @param id 客户端标识
     */
    public boolean hasClient(long id) {
        return id2Client.containsKey(id);
    }

    protected long toId(String host, int port) {
        return ServerID.toId(host, port);
    }

    /**
     * 返回所有的客户端
     *
     * @return 客户端集合
     */
    public List<C> getClients() {
        return clients;
    }

    /**
     * 返回所有客户端的名称
     *
     * @return 客户端标识集合
     */
    public Collection<Long> getIds() {
        return id2Client.keySet();
    }

    /**
     * 返回SocketClient数目，包括未连接的
     */
    public int getCount() {
        return id2Client.size();
    }

    /**
     * 根据客户端标识返回客户端
     *
     * @param id 客户端标识
     */
    public C getClient(long id) {
        return id2Client.get(id);
    }

    /**
     * 根据主机地址和端口返回客户端
     *
     * @param host 主机
     * @param port 端口
     */
    public C getClient(String host, int port) {
        return getClient(toId(host, port));
    }

    class ClientStopThread implements Runnable {
        private Logger log = LoggerFactory.getLogger(ClientStopThread.class);

        public void run() {
            synchronized (clients) {
                //never use foreach instead of for here
                ArrayList<C> newClients = new ArrayList<C>(clients);
                for (C client : newClients) {
                    if (client == null) {
                        continue;
                    }
                    synchronized (client) {
                        if (client instanceof Stoppable) {
                            if (log.isTraceEnabled()) {
                                log.trace("Stop client:" + client);
                            }
                            ((Stoppable)client).stop();
                        }
                        if (client instanceof Destroyable) {
                            if (log.isTraceEnabled()) {
                                log.trace("Destroy client:" + client);
                            }
                            ((Destroyable)client).destroy();
                        }
                    }
                }
            }
        }
    }


    public String toString() {
        return type2List.toString();
    }
}
