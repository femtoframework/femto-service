package org.femtoframework.service.apsis.cube;

import org.femtoframework.bean.Startable;
import org.femtoframework.coin.CoinUtil;
import org.femtoframework.cube.CubeUtil;
import org.femtoframework.cube.spec.HostSpec;
import org.femtoframework.cube.spec.ServerSpec;
import org.femtoframework.service.AbstractConnector;
import org.femtoframework.service.ServerID;
import org.femtoframework.service.apsis.ApsisClient;
import org.femtoframework.service.apsis.ApsisClientManager;
import org.femtoframework.service.client.ClientUtil;
import org.femtoframework.util.ArrayUtil;
import org.femtoframework.util.DataUtil;
import org.femtoframework.util.status.StatusChangeSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Cube连接器，用来连接集群中的其他apsis server
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-11-16 18:03:51
 */
public class CubeConnector extends AbstractConnector
        implements Startable, Runnable {

    protected Logger log = LoggerFactory.getLogger("apsis/cube/connector");
    protected boolean started = false;

    /**
     * 启动
     */
    public void start() {
        if (started) {
            return;
        }

        //启动一个新的线程来连接远程服务器，以加快启动线程的速度，并避免阻塞
        Thread thread = new Thread(this);
        thread.start();
    }


    protected boolean isLaunched() {
        return Boolean.TRUE == System.getProperties().get("cube.system.launched");
    }


    protected void connect(ConnectionSpec conn) {
        String server = conn.getServer();
        String host = conn.getHost();
        List<HostSpec> hosts;
        if ("*".equals(host)) {
            hosts = CubeUtil.getHosts();
        }
        else {
            host = host.replace(',', ';');
            String[] hs = DataUtil.toStrings(host, ';');
            hosts = new ArrayList<>(hs.length);
            for (int i = 0; i < hs.length; i++) {
                hosts.add(CubeUtil.getHost(hs[i]));
            }
        }


        if (!"*".equals(server)) {
            for (HostSpec hd : hosts) {
                ServerSpec sd = hd.getServer(server);
                if (sd != null) {
                    connect(hd, sd, conn);
                }
            }
        }
        else {
            //连接所有拥有该ServerType的HOST
            for (HostSpec host1 : hosts) {
                connect(host1, conn);
            }
        }
    }

    protected void connect(HostSpec hd, URI conn) {
        List<String> servers = hd.getServers();
        for (ServerSpec server : servers) {
            connect(hd, server, conn);
        }
    }

    protected void connect(HostSpec host, ServerSpec server, URI conn) {
        if (host == null || server == null) {
            return;
        }
        String ip = host.getAddress();
        int port = server.getPort();
        String serverType = server.getType();
        String localhost = ServerID.getLocal().getHost();
        boolean local = false;
        if (localhost.equals(ip)) {
            String localType = CubeUtil.getCurrentServer().getType();
            local = localType.equals(serverType);
        }
        if (log.isDebugEnabled()) {
            if (local) {
                log.debug("Connect to local server");
            }
            else {
                log.debug("Connect to " + ip + ':' + port + '#' + serverType);
            }
        }


        String scheme = conn.getString("scheme", "gmpp");
        if (!local) {
            connect(scheme, ip, port, conn);
        }
    }

    protected void connect(String scheme, String ip, int port, ConnectionDefinition conn) {
        ApsisClient client;
        try {
            client = ClientUtil.createClient(new URI(scheme, null, ip, port, null, null, null));
        }
        catch (URISyntaxException e) {
            log.warn("Synatx exception", e);
            return;
        }

        if (conn.hasProperty()) {
            //如果有参数，直接注入给客户
            try {
                BeanUtil.setAttributes(client, conn);
            }
            catch (Exception ex) {
                log.warn("Set property exception", ex);
            }
        }
        if (client instanceof StatusChangeSensor) {
            ((StatusChangeSensor)client).addStatusChangeListener(
                    ((ApsisClientManager) ClientUtil.getManager()).getStatusChangeListener());
        }
        CoinUtil.deployObject(client);
    }

    @Override
    public void run() {
        while (!isLaunched()) {
            Jade.sleep(100);
        }
        ServerSpec sd = CubeUtil.getCurrentServer();
        if (sd == null) {
            return;
        }
        if (log.isDebug()) {
            log.debug("Start CubeConnector...");
        }
        ConnectionDefinition[] conns = sd.getConnections();
        if (ArrayUtil.isValid(conns)) {
            for (ConnectionDefinition conn : conns) {
                connect(conn);
            }
        }
        started = true;
        if (log.isDebugEnabled()) {
            log.debug("CubeConnector started!");
        }
    }
}

