package org.femtoframework.service;

import org.femtoframework.bean.NamedBean;

/**
 * 服务器连接器，用来跟服务器连接，调用服务器的服务<br>
 * 创建顺序：<br>
 * <p/>
 * 　创建对象ConnectorFactory#createConnector<br>
 * 邦定服务器#bind<br>
 * 设置配置#setConfig<br>
 * 初始化#init<br>
 * 启动#start<br>
 *
 * @author fengyun
 * @version Feb 11, 2003 8:41:04 PM
 */
public interface Connector extends NamedBean
{
    /**
     * 返回服务器，从该连接上来的消息会交给该服务器来处理
     *
     * @return 消息处理服务器
     */
    Server getServer();

    /**
     * 设置消息处理服务器
     *
     * @param server 服务器
     */
    void setServer(Server server);
}
