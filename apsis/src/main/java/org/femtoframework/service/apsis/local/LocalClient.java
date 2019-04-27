package org.femtoframework.service.apsis.local;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.femtoframework.net.message.*;
import org.femtoframework.service.ServerID;
import org.femtoframework.service.apsis.ApsisClient;
import org.femtoframework.service.apsis.ApsisServerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LocalClient
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-11-15 11:31:11
 */
public class LocalClient implements ApsisClient
{
    /* 实例定义 */
    private static LocalClient instance = null;
    private MessageRegistry registry = MessageRegistryUtil.getRegistry();

    @Ignore
    private Logger logger = LoggerFactory.getLogger("apsis/local/client");

    /**
     * 提交消息
     *
     * @param message
     * @return
     */
    public static RequestFuture submitMessage(RequestMessage message)
    {
        return getInstance().submit(message);
    }

    /**
     * 获取单例方法实现
     *
     * @return 单例
     */
    public static ApsisClient getInstance()
    {
        if (instance == null) {
            instance = new LocalClient();
        }
        return instance;
    }

    private String type;

    private LocalClient()
    {
        type = System.getProperty("cube.system.type");
    }


    /**
     * 返回目标主机地址
     *
     * @return 目标主机地址
     */
    public String getHost()
    {
        return ServerID.getLocal().getHost();
    }

    /**
     * 设置目标主机地址
     *
     * @param host 主机地址
     */
    public void setHost(String host)
    {
        throw new IllegalAccessError();
    }

    /**
     * 返回目标主机端口
     *
     * @return 目标端口
     */
    public int getPort()
    {
        return ServerID.getLocal().getPort();
    }

    /**
     * 发送消息
     *
     * @param message 消息
     * @throws IllegalArgumentException 消息无效的时候抛出
     */
    public MessageFuture send(Object message)
    {
        if (message instanceof LocalReqRepPair) {
            LocalReqRepPair pair = (LocalReqRepPair)message;
            pair.setDone(true);
            return new LocalRequestFuture(pair);
        }
        else {
            int type = registry.getType(message);
            if (type == -1) {
                throw new IllegalArgumentException("Invalid request message:" + message);
            }
            MessageMetadata mm = registry.getMetadata(type);
            LocalReqRepPair reqRepPair = new LocalReqRepPair(message, registry, type);
            reqRepPair.setMessageSender(this);
            LocalRequestFuture lrf = new LocalRequestFuture(reqRepPair);
            try {
                ((MessageListener) ApsisServerUtil.getServer()).onMessage(mm, reqRepPair);
            }
            catch (Throwable t) {
                //如果运行过出错了，那么也需要设置完成,以免客户端无畏等待
                if (logger.isWarnEnabled()) {
                    logger.warn("Exception", t);
                }
//            reqRepPair.getResponse();
                reqRepPair.setDone(true);
            }
            return lrf;
        }
//        if (message instanceof LocalReqRepPair) {
//            ((LocalReqRepPair) message).setDone(true);
//        }
//        return new LocalMessageFuture();
    }

    /**
     * 发送请求
     *
     * @param message 消息
     * @throws IllegalArgumentException 消息无效的时候抛出
     * @throws MessageWindowFullException
     *                                  消息窗口满的时候抛出
     */
    public RequestFuture submit(RequestMessage message)
    {
        int type = registry.getType(message);
        if (type == -1) {
            throw new IllegalArgumentException("Invalid request message:" + message);
        }
        MessageMetadata mm = registry.getMetadata(type);
        LocalReqRepPair reqRepPair = new LocalReqRepPair(message, registry, type);
        reqRepPair.setMessageSender(this);
        LocalRequestFuture lrf = new LocalRequestFuture(reqRepPair);
        try {
            ((MessageListener)ApsisServerUtil.getServer()).onMessage(mm, reqRepPair);
        }
        catch (Throwable t) {
            if (logger.isWarnEnabled()) {
                logger.warn("Exception", t);
            }
            //如果运行过出错了，那么也需要设置完成,以免客户端无畏等待
//            reqRepPair.getResponse();
            reqRepPair.setDone(true);
        }
        return lrf;
    }

    /**
     * 设置目标主机端口
     *
     * @param port 主机端口
     */
    public void setPort(int port)
    {
        throw new IllegalAccessError();
    }

    /**
     * 返回对应的服务器类型，对应于一项应用：webmail admin ...
     *
     * @return 对应的服务器类型
     */
    public String getServerType()
    {
        return type;
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

    /**
     * 返回对应的服务器标识
     *
     * @return 对应的服务器标识
     */
    public long getId()
    {
        return ServerID.getLocal().getId();
    }
}
