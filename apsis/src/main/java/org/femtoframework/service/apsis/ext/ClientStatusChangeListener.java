package org.femtoframework.service.apsis.ext;

import org.femtoframework.net.comm.CommConstants;
import org.femtoframework.net.message.MessageLayer;
import org.femtoframework.service.apsis.ApsisClient;
import org.femtoframework.service.event.GenericEvent;
import org.femtoframework.util.status.StatusChangeListener;
import org.femtoframework.util.status.StatusEvent;

/**
 * 客户端状态侦听者
 *
 * @author fengyun
 * @version 1.00 2005-11-21 16:13:29
 */
public class ClientStatusChangeListener implements StatusChangeListener
{
    private SimpleApsisServer server;

    public ClientStatusChangeListener(SimpleApsisServer server)
    {
        this.server = server;
    }

    /**
     * 接收到状态改变事件，当有ApsisClient创建的时候触发，发送ServerMetadata
     */
    public void changed(StatusEvent event)
    {
        Object src = event.getSource();
        if (src instanceof ApsisClient) {
            final ApsisClient client = (ApsisClient) src;
            int status = event.getStatus();
            if (status == CommConstants.STATUS_ALIVE) {
                if (client instanceof MessageLayer) {
                    ((MessageLayer) client).setMessageListener(server);
                }
                //发送本地的Server metadata 到远程服务器
                try {
                    GenericEvent ge = new GenericEvent("+/system/server_metadata_listener",
                                                       "server_metadata", server.getMetadata());
                    client.send(ge);
                }
                catch (Throwable t) {
                    LogUtil.error("Send Server Metadata error", t);
                }
            }
        }
    }
}
