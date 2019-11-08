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

import java.net.URI;
import java.net.URISyntaxException;
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

    private int interval = 10 * 1000;

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
        return DataUtil.getBoolean(System.getProperties().get("cube.system.launched"), false);
    }


    protected void connect(SystemSpec systemSpec, ConnectionSpec conn) {
        //TODO host list should be loaded from other resources such as K8S

//        String server = conn.getServerType();
//        String host = conn.getHost();
//        List<HostSpec> hosts;
//        if ("*".equals(host)) {
//            hosts = CubeUtil.getHosts();
//        }
//        else {
//            host = host.replace(',', ';');
//            String[] hs = DataUtil.toStrings(host, ';');
//            hosts = new ArrayList<>(hs.length);
//            for (int i = 0; i < hs.length; i++) {
//                hosts.add(CubeUtil.getHost(hs[i]));
//            }
//        }
//
//        for (HostSpec hd : hosts) {
//            connect(systemSpec, hd, server, conn);
//        }
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

        String scheme = conn.getUri().getScheme();
        if (scheme == null) {
            scheme = "gmpp";
        }
        if (!local) {
            connect(scheme, ip, port, conn);
        }
    }

    protected void connect(String scheme, String ip, int port, ConnectionSpec conn) {
        ApsisClient client = ClientUtil.getClient(ip, port, false);
        if (client == null || !client.isAlive()) {
            URI origUri = conn.getUri();
            URI uri = null;
            try {
                uri = new URI(scheme, "", ip, port,origUri.getPath(), origUri.getQuery(),  origUri.getFragment());
            } catch (URISyntaxException e) {
                throw new IllegalStateException("URI syntax error");
            }

            client = ClientUtil.createClient(uri);
            if (log.isDebugEnabled()) {
                log.debug("Connect to " + ip + ':' + port + '#' + conn.getServerType());
            }

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

        keepAlive();

        started = true;
        if (log.isInfoEnabled()) {
            log.info("CubeConnector started!");
        }

        //Give 1 minutes to let the connections created
        try {
            Thread.sleep(interval*6);
        }
        catch(Exception ex) {
            //Ignore
        }

        while(started) {
            try {
                Thread.sleep(interval);
            }
            catch(Exception ex) {
                //Ignore
            }

            keepAlive();
        }
    }

    public void keepAlive() {
        ServerSpec sd = CubeUtil.getCurrentServer();
        List<ConnectionSpec> conns = sd.getConnectionSpecs();
        if (!conns.isEmpty()) {
            for (ConnectionSpec conn : conns) {
                connect(CubeUtil.getSystemSpec(), conn);
                if (log.isInfoEnabled()) {
                    log.info("Connection to:" + conn.getUri() + " created.");
                }
            }
        }
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}

