package org.femtoframework.service.apsis.gmpp;

import org.femtoframework.bean.NamedBean;
import org.femtoframework.bean.Stoppable;
import org.femtoframework.net.gmpp.GmppCommClient;
import org.femtoframework.service.RemoteClient;
import org.femtoframework.service.ServerID;
import org.femtoframework.service.apsis.ApsisClient;

import java.io.Closeable;
import java.io.IOException;

/**
 * Gmpp客户端
 *
 * @author fengyun
 * @version 1.00 2005-5-22 14:00:16
 */
public class GmppClient extends GmppCommClient
    implements RemoteClient, Stoppable, ApsisClient, NamedBean, Closeable
{

    public static final String DEFAULT_OBJECT_CODEC = "apsis";

    /**
     * 服务器标识
     */
    private long id;

    /**
     * 服务器类型
     */
    private String serverType;

    /**
     * 构造
     */
    public GmppClient()
    {
        super();
        setConnectionClass(GmppConnectionEx.class);
        setCodec(DEFAULT_OBJECT_CODEC);
    }

    /**
     * 停止
     */
    public void stop()
    {
        try {
            close();
        }
        catch (IOException e) {
            logger.warn("Closing exception", e);
        }
    }

    /**
     * 返回对应的服务器类型
     *
     * @return 对应的服务器类型
     */
    public String getServerType()
    {
        if (serverType == null) {
            serverType = getRemoteType();
        }
        return serverType;
    }

    /**
     * 返回对应的服务器标识
     *
     * @return 对应的服务器标识
     */
    public long getId()
    {
        if (id == 0) {
            id = ServerID.toId(getRemoteHost(), getRemotePort());
        }
        return id;
    }

    /**
     * 设置服务器类型
     *
     * @param serverType 服务器类型
     */
    public void setServerType(String serverType)
    {
        this.serverType = serverType;
    }

    private String name = null;

    /**
     * 返回对象名称
     *
     * @return 服务器名称
     */
    public String getName()
    {
        if (name == null) {
            name = serverType + "@" + getHost() + ":" + getPort();
        }
        return name;
    }
}
