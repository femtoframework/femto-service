package org.femtoframework.service;


import org.femtoframework.bean.Nameable;

/**
 * 抽象连接器
 *
 * @author fengyun
 * @version 1.00 2005-5-22 13:42:20
 */
public class AbstractConnector implements Connector, Nameable {

    private String name;
    private Server server;

    /**
     * 返回服务器，从该连接上来的消息会交给该服务器来处理
     *
     * @return 消息处理服务器
     */
    public Server getServer() {
        return server;
    }

    /**
     * 设置消息处理服务器
     *
     * @param server 服务器
     */
    public void setServer(Server server) {
        this.server = server;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
