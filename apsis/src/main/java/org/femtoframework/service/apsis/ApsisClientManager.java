package org.femtoframework.service.apsis;

import org.femtoframework.service.client.ClientManager;
import org.femtoframework.util.status.StatusChangeListener;
import org.femtoframework.util.status.StatusChangeSensor;

/**
 * Apsis客户端映射
 *
 * @author fengyun
 * @version 1.00 2005-6-5 1:38:59
 */
public interface ApsisClientManager
    extends ClientManager<ApsisClient>, StatusChangeSensor
{
    /**
     * 返回状态修改侦听者
     *
     * @return Status Change Listener
     */
    StatusChangeListener getStatusChangeListener();
}
