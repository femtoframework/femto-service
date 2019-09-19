package org.femtoframework.service.apsis.cube;

import org.femtoframework.bean.BeanStage;
import org.femtoframework.bean.Startable;
import org.femtoframework.bean.info.BeanInfo;
import org.femtoframework.bean.info.BeanInfoUtil;
import org.femtoframework.bean.info.PropertyInfo;
import org.femtoframework.coin.CoinUtil;
import org.femtoframework.cube.CubeUtil;
import org.femtoframework.cube.spec.ConnectionSpec;
import org.femtoframework.cube.spec.HostSpec;
import org.femtoframework.cube.spec.ServerSpec;
import org.femtoframework.cube.spec.SystemSpec;
import org.femtoframework.parameters.Parameters;
import org.femtoframework.service.AbstractConnector;
import org.femtoframework.service.ServerID;
import org.femtoframework.service.apsis.ApsisClient;
import org.femtoframework.service.apsis.ApsisClientManager;
import org.femtoframework.service.client.ClientUtil;
import org.femtoframework.util.DataUtil;
import org.femtoframework.util.StringUtil;
import org.femtoframework.util.status.StatusChangeSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Cube连接器，用来连接集群中的其他apsis server
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-11-16 18:03:51
 */
public class CubeConnector extends AbstractConnector implements Startable, Runnable {

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


    protected void connect(SystemSpec systemSpec, ConnectionSpec conn) {
        String server = conn.getServerType();
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

        for (HostSpec hd : hosts) {
            connect(systemSpec, hd, server, conn);
        }
    }

    protected void connect(SystemSpec system, HostSpec hd, String serverType, ConnectionSpec conn) {
        List<String> servers = hd.getServers();
        boolean all = "*".equals(serverType);
        for (String server : servers) {
            if (all || StringUtil.equals(server, serverType)) {
                ServerSpec serverSpec = system.getServer(serverType);
                if (serverSpec != null) {
                    connect(hd, serverSpec, conn);
                }
                else {
                    log.error("No such server:" + server);
                }
            }
        }
    }

    protected void connect(HostSpec host, ServerSpec server, ConnectionSpec conn) {
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


        String scheme = conn.getUri().getScheme();
        if (scheme == null) {
            scheme = "gmpp";
        }
        if (!local) {
            connect(scheme, ip, port, conn);
        }
    }

    protected void connect(String scheme, String ip, int port, ConnectionSpec conn) {
        ApsisClient client = ClientUtil.createClient(conn.getUri());

        if (!conn.getParameters().isEmpty()) {
            //如果有参数，直接注入给客户
            BeanInfo beanInfo = BeanInfoUtil.getBeanInfo(client.getClass());
            Parameters<Object> parameters = conn.getParameters();
            for(String key: parameters.keySet()) {
                Object value = parameters.get(key);
                PropertyInfo propertyInfo = beanInfo.getProperty(key);
                if (propertyInfo != null) {
                    try {
                        propertyInfo.invokeSetter(conn, value);
                    }
                    catch (Exception ex) {
                        log.warn("Set property exception", ex);
                    }
                }
            }
        }
        if (client instanceof StatusChangeSensor) {
            ((StatusChangeSensor)client).addStatusChangeListener(
                    ((ApsisClientManager) ClientUtil.getManager()).getStatusChangeListener());
        }
        CoinUtil.getModule().getLifecycleStrategy().ensure(client, BeanStage.START);
    }

    @Override
    public void run() {
        while (!isLaunched()) {
            try {
                Thread.sleep(100);
            }
            catch(Exception ex) {
                //Ignore
            }
        }
        ServerSpec sd = CubeUtil.getCurrentServer();
        if (sd == null) {
            return;
        }
        if (log.isInfoEnabled()) {
            log.info("Start CubeConnector...");
        }
        List<ConnectionSpec> conns = sd.getConnectionSpecs();
        if (!conns.isEmpty()) {
            for (ConnectionSpec conn : conns) {
                connect(CubeUtil.getSystemSpec(), conn);
            }
        }
        started = true;
        if (log.isInfoEnabled()) {
            log.info("CubeConnector started!");
        }
    }
}

