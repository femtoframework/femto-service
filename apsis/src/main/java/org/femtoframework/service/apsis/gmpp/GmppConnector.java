package org.femtoframework.service.apsis.gmpp;

import org.femtoframework.bean.BeanPhase;
import org.femtoframework.bean.Initializable;
import org.femtoframework.bean.LifecycleMBean;
import org.femtoframework.bean.Startable;
import org.femtoframework.bean.annotation.Ignore;
import org.femtoframework.bean.annotation.Property;
import org.femtoframework.cube.CubeUtil;
import org.femtoframework.net.message.MessageListener;
import org.femtoframework.service.AbstractConnector;
import org.femtoframework.service.Server;
import org.femtoframework.service.apsis.ApsisClientManager;
import org.femtoframework.service.client.ClientUtil;
import org.femtoframework.util.status.StatusChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gmpp连接器，用来接入集群众其它Server
 *
 * @author fengyun
 * @version 1.00 2005-5-22 13:41:36
 */
public class GmppConnector extends AbstractConnector
    implements LifecycleMBean {
    private MessageListener messageListener;
    private StatusChangeListener statusChangeListener;
    private ApsisClientManager clientMap;

    protected GmppSocketHandler socketHandler;

    /**
     */
    @Property("daemon")
    private boolean daemon = false;


    private String supportVersions;
    private String supportCodecs;

    /**
     * 返回消息侦听者
     *
     * @return 消息侦听者
     */
    public MessageListener getMessageListener() {
        return messageListener;
    }

    /**
     * 设置消息处理服务器
     *
     * @param server 服务器
     */
    public void setServer(Server server) {
        if (!(server instanceof MessageListener)) {
            throw new IllegalArgumentException("Can't bind a gmpp connector to a server without MessageListener");
        }
        messageListener = ((MessageListener)server);
        super.setServer(server);
    }

    public StatusChangeListener getStatusChangeListener() {
        return statusChangeListener;
    }

    /**
     * 返回客户端映射
     *
     * @return 客户端映射
     */
    public ApsisClientManager getClientMap() {
        return clientMap;
    }

    /**
     * 启动
     */
    public void _doStart() {
        socketHandler = new GmppSocketHandler(this);
        socketHandler.setLogger(logger);
        socketHandler.setDaemon(daemon);

        if (supportCodecs != null) {
            socketHandler.setSupportedCodecs(supportCodecs);
        }

        if (supportVersions != null) {
            socketHandler.setSupportedVersions(supportVersions);
        }

        socketHandler.start();

        registerSocketHandler(socketHandler);

        //确认启动Cube App Server
        CubeUtil.getAppServer();
    }


    protected void registerSocketHandler(GmppSocketHandler socketHandler) {
        CubeUtil.getSocketHandlerFactory().addHandler(socketHandler);
    }

    @Ignore
    private Logger logger = LoggerFactory.getLogger("apsis/gmpp/connector");

    /**
     * 设置日志
     *
     * @param log Logger
     */
    public void setLogger(Logger log) {
        this.logger = log;
    }

    /**
     * 返回日志
     *
     * @return Logger
     */
    public Logger getLogger() {
        return logger;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public void setSupportedVersions(String version) {
        socketHandler.setSupportedVersions(version);
    }

    public void setSupportedCodecs(String codecs) {
        socketHandler.setSupportedCodecs(codecs);
    }

    private BeanPhase beanPhase = BeanPhase.DISABLED;

    /**
     * Implement method of getPhase
     *
     * @return BeanPhase
     */
    @Override
    public BeanPhase _doGetPhase() {
        return beanPhase;
    }

    /**
     * Phase setter for internal
     *
     * @param phase BeanPhase
     */
    @Override
    public void _doSetPhase(BeanPhase phase) {
        this.beanPhase = phase;
    }

    /**
     * 初始化
     */
    public void _doInit() {
        //只有对等的服务器才可以用GMPP协议的服务器端连接器
        clientMap = ClientUtil.getManager();
        //Peer服务器会关注客户端的状态
        statusChangeListener = clientMap.getStatusChangeListener();
    }

    public GmppSocketHandler getSocketHandler() {
        return socketHandler;
    }

    public String getSupportVersions() {
        return supportVersions;
    }

    public void setSupportVersions(String supportVersions) {
        this.supportVersions = supportVersions;
    }

    public String getSupportCodecs() {
        return supportCodecs;
    }

    public void setSupportCodecs(String supportCodecs) {
        this.supportCodecs = supportCodecs;
    }
}
