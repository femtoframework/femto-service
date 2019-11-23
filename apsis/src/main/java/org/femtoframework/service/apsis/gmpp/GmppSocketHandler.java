package org.femtoframework.service.apsis.gmpp;

import org.femtoframework.bean.info.BeanInfoUtil;
import org.femtoframework.coin.metrics.MetricsUtil;
import org.femtoframework.io.IOUtil;
import org.femtoframework.net.gmpp.GmppConnection;
import org.femtoframework.net.gmpp.GmppConstants;
import org.femtoframework.net.gmpp.GmppPacketProtocol;
import org.femtoframework.net.socket.bifurcation.BifurcatedSocketHandler;
import org.femtoframework.pattern.Loggable;
import org.femtoframework.service.ServerID;
import org.femtoframework.service.apsis.ApsisClient;
import org.femtoframework.service.apsis.ApsisClientManager;
import org.femtoframework.util.DataUtil;
import org.femtoframework.util.thread.ExecutorUtil;
import org.slf4j.Logger;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * NAS Socket处理器
 *
 * @author fengyun
 * @version Feb 12, 2003 7:17:22 PM
 */
public class GmppSocketHandler
    extends BifurcatedSocketHandler implements Loggable, Runnable
{
    /**
     * 日志
     */
    private Logger log;

    /**
     * 客户端列表
     */
    private final ApsisClientManager clientMap;

    private byte[] supportedVersions = new byte[]{GmppConstants.VERSION, GmppConstants.VERSION_3};

    private HashSet<String> supportedCodecs = new HashSet<String>();

    private boolean daemon = false;

    private GmppConnector connector;

    private String secure = "";

    /**
     * 支持JRMP和NIEP
     */
    {
        supportedCodecs.add("jrmp");
        supportedCodecs.add("niep");
        supportedCodecs.add("apsis");
    }

    /**
     * 构造
     *
     * @param connector
     */
    public GmppSocketHandler(GmppConnector connector)
    {
        setBifurcation(ApsisGmppConstants.GMPP_BIFURCATION);
        this.connector = connector;
        this.clientMap = connector.getClientMap();
    }

    /**
     * 处理Socket
     *
     * @param socket Socket
     */
    public void handleSocket(Socket socket)
    {
        GmppConnection conn = new GmppConnection();
        GmppPacketProtocol packetProtocol = new GmppPacketProtocol();
        try {
            conn.setProtocol(packetProtocol);
            conn.setSocket(socket);
            conn.setSecure(secure);
            conn.accept(supportedVersions, supportedCodecs);
        }
        catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.debug("Exception", t);
            }
            IOUtil.close(conn);
            return;
        }

        String remoteHost = conn.getRemoteHost();
        int remotePort = conn.getRemotePort();
        String remoteType = conn.getRemoteType();
        String codec = conn.getCodec();
        //Reset codec
        packetProtocol.setCodec(codec);

        long serverId = ServerID.toId(remoteHost, remotePort);
        try {
            GmppClient client = (GmppClient)clientMap.getClient(serverId);
            if (client == null) {
                synchronized (clientMap) {
                    client = (GmppClient)clientMap.getClient(serverId);
                    if (client == null) {
                        //创建
                        GmppClient gClient = new GmppClient();
                        gClient.setAutoConnect(false);
                        gClient.setMessageListener(connector.getMessageListener());
                        gClient.setHost(remoteHost);
                        gClient.setPort(remotePort);
                        gClient.setRemoteHost(remoteHost);
                        gClient.setRemotePort(remotePort);
                        gClient.setRemoteVersion(conn.getRemoteVersion());
                        gClient.setRemoteType(conn.getRemoteType());
                        gClient.setCodec(codec);
                        gClient.setDaemon(daemon);
                        gClient.setServerType(remoteType);
                        gClient.addStatusChangeListener(connector.getStatusChangeListener());
                        gClient.setLogger(log);
                        gClient.init();
                        gClient.start();

                        client = gClient;
                    }
                }
            }
            client.addConnection(conn);
        }
        catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.debug("Exception", t);
            }
            IOUtil.close(conn);
            return;
        }
        if (log.isInfoEnabled()) {
            log.info("Add connection:" + conn);
        }
    }

    /**
     * 处理Socket
     *
     * @param socket Socket
     */
    public void handle(Socket socket)
    {
        handleSocket(socket);
    }

    /**
     * 设置日志
     *
     * @param log Logger
     */
    public void setLogger(Logger log)
    {
        this.log = log;
    }

    /**
     * 返回日志
     *
     * @return Logger
     */
    public Logger getLogger()
    {
        return log;
    }


    public boolean isDaemon()
    {
        return daemon;
    }

    public void setDaemon(boolean daemon)
    {
        this.daemon = daemon;
    }

    private List<GmppClient> toClose = new ArrayList<GmppClient>();

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run()
    {
        try {
            if (!toClose.isEmpty()) {
                for (int i = toClose.size() - 1; i >= 0; i--) {
                    GmppClient gc = toClose.get(i);
                    if (!gc.isAlive()) {
                        gc.close();
                    }
                    toClose.remove(i);
                }
            }

            List<ApsisClient> list = clientMap.getClients();
            List<ApsisClient> cloned = new ArrayList<>(list);
            for (ApsisClient client : cloned) {
                if (client instanceof GmppClient) {
                    GmppClient gc = (GmppClient)client;
                    if (!gc.isAlive()) { //等待下一次时间来关闭
                        toClose.add(gc);
                    }
                }
            }
        }
        catch (Throwable ex) {
        }
    }

    private ScheduledFuture future;


    public void setSupportedVersions(String version) {
        String[] array = DataUtil.toStrings(version, ',');
        byte[] versions = new byte[array.length];
        for(int i = 0; i < array.length; i ++) {
            versions[i] = Byte.parseByte(array[i], 16);
        }
        this.supportedVersions = versions;
    }

    public void setSupportedCodecs(String codecs) {
        HashSet<String> set = new HashSet<>();
        String[] array = DataUtil.toStrings(codecs, ',');
        Collections.addAll(set, array);
        this.supportedCodecs = set;
    }

    /**
     * 实际启动实现
     */
    public void _doStart()
    {
        super._doStart();
        future = ExecutorUtil.scheduleWithFixedDelay(this, 60000, 60000);
    }

    /**
     * 实际停止实现
     */
    public void _doStop()
    {
        super._doStop();
        if (future != null) {
            future.cancel(true);
        }
    }

    public String getSecure() {
        return secure;
    }

    public void setSecure(String secure) {
        this.secure = secure;
    }
}

