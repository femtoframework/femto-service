package org.femtoframework.service.apsis.client;

import org.femtoframework.net.message.MessageFuture;
import org.femtoframework.net.message.RequestFuture;
import org.femtoframework.net.message.RequestMessage;
import org.femtoframework.service.apsis.ApsisClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 多个客户端的组合，用来同时将请求发送到多个目标
 * 不同于组播，这是可以选择的客户端
 * todo
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-11-30 12:06:25
 */
public class MultiClient implements ApsisClient
{
    private List<ApsisClient> clients = null;

    public MultiClient(List<ApsisClient> clients)
    {
        this.clients = clients;
    }

    /**
     * 返回对应的服务器类型，对应于一项应用：webmail admin ...
     *
     * @return 对应的服务器类型
     */
    public String getServerType()
    {
        return null;
    }

    /**
     * 返回对应的服务器标识
     *
     * @return 对应的服务器标识
     */
    public long getId()
    {
        return 0;
    }

    /**
     * 返回目标主机地址
     *
     * @return 目标主机地址
     */
    public String getHost()
    {
        return null;
    }

    /**
     * 返回目标主机端口
     *
     * @return 目标主机端口
     */
    public int getPort()
    {
        return 0;
    }

    /**
     * Multi clients
     *
     * @return
     */
    public List<ApsisClient> getClients() {
        return clients;
    }
    /**
     * 发送消息
     *
     * @param message 消息
     * @throws IllegalArgumentException 消息无效的时候抛出
     */
    public MessageFuture send(Object message)
    {
        return null;
    }

    /**
     * 发送请求
     *
     * @param message 消息
     * @throws IllegalArgumentException 消息无效的时候抛出
     */
    public RequestFuture submit(RequestMessage message)
    {
        List<RequestFuture> rf = new ArrayList<RequestFuture>(clients.size());
        for (ApsisClient client : clients) {
            rf.add(client.submit(message));
        }
        return new MultiRequestFuture(rf);
    }

    /**
     * 客户端是否还活着
     *
     * @return 是否还活着
     */
    public boolean isAlive()
    {
        return true;
    }
}
