package org.femtoframework.cube.spec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 服务器定义
 *
 * @author fengyun
 * @version 1.00 2005-2-5 11:42:25
 */
public class ServerSpec
{
    private String type;
    private int port;

    private List<String> connections;

    private static Logger log = LoggerFactory.getLogger(ServerSpec.class);

    /**
     * 返回服务器类型
     *
     * @return 服务器类型
     */
    public String getType() {
        return type;
    }

    /**
     * 返回服务端口
     *
     * @return 服务端口
     */
    public int getPort() {
        return port;
    }

    /**
     * 返回所有连接
     *
     * @return 所有的连接
     */
    public List<String> getConnections() {
        if (connections != null) {
            return connections;
        }
        else {
            return Collections.emptyList();
        }
    }

    public List<ConnectionSpec> getConnectionSpecs() {
        List<String> connStrs = getConnections();
        List<ConnectionSpec> specs = new ArrayList<>(connStrs.size());
        for(String str: connStrs) {
            try {
                specs.add(new ConnectionSpec(str));
            }
            catch(URISyntaxException use) {
                log.warn("Connection URI syntax error:" + use.getInput(), use);
            }
        }
        return specs;
    }

    /**
     * 设置服务器类型
     *
     * @param type 类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 　设置端口
     *
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    public void setConnections(List<String> connections) {
        this.connections = connections;
    }
}
