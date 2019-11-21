package org.femtoframework.service.apsis.gmpp;

import org.femtoframework.service.apsis.ApsisClient;
import org.femtoframework.service.apsis.ApsisClientFactory;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * GMPP 客户端工厂
 *
 * @author fengyun
 * @version 1.00 2005-7-25 17:36:05
 */
public class GmppApsisClientFactory implements ApsisClientFactory {

    /**
     * 创建客户端
     *
     * @param uri URI地址
     * @return 客户端对象仅仅执行到配置的步骤，不调用Lifecycle相关的方法
     */
    public ApsisClient createClient(URI uri) {
        GmppClient client = new GmppClient();
        client.setHost(uri.getHost());
        client.setRemoteHost(uri.getHost());
        client.setPort(uri.getPort());
        client.setRemotePort(uri.getPort());
        client.setLogger(LoggerFactory.getLogger(client.getClass()));
        return client;
    }
}
