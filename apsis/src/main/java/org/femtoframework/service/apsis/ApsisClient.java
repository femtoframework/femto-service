package org.femtoframework.service.apsis;

import org.femtoframework.net.message.MessageSender;
import org.femtoframework.service.Client;

/**
 * Apsis客户端
 *
 * @author fengyun
 * @version 1.00 2005-6-5 0:01:03
 */
public interface ApsisClient
    extends Client, MessageSender
{
    /**
     * 客户端是否还活着
     *
     * @return 是否还活着
     */
    boolean isAlive();
}
