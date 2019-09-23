package org.femtoframework.service.apsis.client;

import org.femtoframework.bean.Startable;
import org.femtoframework.cube.CubeUtil;
import org.femtoframework.cube.spec.ServerSpec;
import org.femtoframework.net.HostPort;
import org.femtoframework.net.comm.CommConstants;
import org.femtoframework.service.RemoteClient;
import org.femtoframework.service.apsis.ApsisClient;
import org.femtoframework.service.apsis.ApsisClientManager;
import org.femtoframework.service.apsis.balance.ext.SimpleBalancer;
import org.femtoframework.service.apsis.gmpp.GmppApsisClientFactory;
import org.femtoframework.service.apsis.local.LocalClient;
import org.femtoframework.service.client.AbstractClientManager;
import org.femtoframework.service.client.Balancer;
import org.femtoframework.service.client.ClientList;
import org.femtoframework.util.status.StatusChangeListener;
import org.femtoframework.util.status.StatusChangeSensor;
import org.femtoframework.util.status.StatusChangeSupport;
import org.femtoframework.util.status.StatusEvent;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Apsis客户端映射
 *
 * @author fengyun
 * @version 1.00 2005-6-5 1:40:35
 */
public class SimpleApsisClientManager extends AbstractClientManager<ApsisClient>
    implements ApsisClientManager, StatusChangeSensor, StatusChangeListener {

    private SimpleBalancer balancer;

    /**
     * 构造
     */
    public SimpleApsisClientManager() {
        balancer = new SimpleBalancer(this);
        addClient(LocalClient.getInstance());//默认加入本地客户端
    }

    /**
     * 返回状态修改侦听者
     *
     * @return Status Change Listener
     */
    public StatusChangeListener getStatusChangeListener() {
        return support;
    }

    /**
     * 接收到状态改变事件
     */
    public void changed(StatusEvent event) {
        Object src = event.getSource();
        if (src instanceof ApsisClient) {
            ApsisClient client = (ApsisClient)src;
            int status = event.getStatus();
            switch (status) {
                case CommConstants.STATUS_ALIVE:
                    addClient(client);
                    break;
                case CommConstants.STATUS_NO_CONNECTION:
                case CommConstants.STATUS_CLOSED:
                    if (!(client instanceof RemoteClient &&
                          !((RemoteClient)client).isLongTerm())) {
                        //如果不是短连接的，不要删除服务器
                        removeClient(client);
                    }
                    break;
            }
        }
    }

    private ApsisClient createDefaultClient(String host, int port) {
        InetSocketAddress address = new InetSocketAddress(host, port); //CubeUtil.getMulticastAddress(host, port);
        ApsisClient client = null;
        String scheme = "gmpp";
        InetAddress addr = address.getAddress();
//        if (addr.isMulticastAddress()) { //请求的是组播
//            scheme = "group";
//        }
        try {
            client = createClient(new URI(scheme, null, addr.getHostAddress(), address.getPort(),
                null, null, null));
        }
        catch (URISyntaxException e) {
            //Never
        }
        if (client instanceof StatusChangeSensor) {
            ((StatusChangeSensor)client).addStatusChangeListener(getStatusChangeListener());
        }
        if (client instanceof Startable) {
            ((Startable)client).start();
        }
        return client;
    }

    /**
     * 根据客户端标识返回客户端
     *
     * @param id     客户端标识
     * @param create 如果不存在，是否自动创建
     */
    public ApsisClient getClient(long id, boolean create) {
        HostPort hp = HostPort.toHostPort(id);
        String host = hp.getHost();
        int port = hp.getPort();
        return getClient(host, port, create);
    }

    @Override
    public Balancer<ApsisClient> getBalancer() {
        return balancer;
    }

    /**
     * 根据主机地址和端口返回客户端
     *
     * @param host   主机
     * @param port   端口
     * @param create 如果不存在是否自动创建
     */
    public ApsisClient getClient(String host, int port, boolean create) {
        ApsisClient client = getClient(host, port);
        if (client == null && create) {
            synchronized (this) {
                client = getClient(host, port);
                if (client == null) {
                    client = createDefaultClient(host, port);
                }
                addClient(client);
            }
        }
        return client;
    }

    @Override
    public int getClientCount(String appType) {
        ServerSpec serverSpec = CubeUtil.getServer(appType);
        if (serverSpec != null) {
            ClientList list = getClients(serverSpec.getType());
            return list != null ? list.getCount() : 0;
        }
        return 0;
    }

    @Override
    public ApsisClient createClient(URI uri) {
        String scheme = uri.getScheme();
        if ("gmpp".equals(scheme)) {
            return GmppApsisClientFactory.INSTANCE.createClient(uri);
        }
        else {
            throw new IllegalArgumentException("Unsupported scheme:" + scheme);
        }
    }

    private StatusChangeSupport support = new StatusChangeSupport();

    {
        support.addStatusChangeListener(this);
    }

    /**
     * 设置事件源和侦听者
     *
     * @param listener 侦听者
     */
    public void addStatusChangeListener(StatusChangeListener listener) {
        support.addStatusChangeListener(listener);
    }

    /**
     * 删除状态改变侦听者
     *
     * @param listener 侦听者
     */
    public void removeStatusChangeListener(StatusChangeListener listener) {
        support.removeStatusChangeListener(listener);
    }

}
