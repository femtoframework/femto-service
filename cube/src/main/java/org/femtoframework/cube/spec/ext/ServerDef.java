package org.femtoframework.cube.spec.ext;

import org.femtoframework.bean.Initializable;
import org.femtoframework.bean.exception.InitializeException;
import org.femtoframework.cube.spec.ConnectionSpec;
import org.femtoframework.cube.spec.ServerSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 服务器
 *
 * @author fengyun
 * @version 1.00 2005-3-7 14:40:58
 */
public class ServerDef implements ServerSpec, Initializable {
    private String type;
    private int port;

    private List<ConnectionSpec> connections;

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
    public List<ConnectionSpec> getConnections() {
        if (connections != null) {
            return connections;
        }
        else {
            return Collections.emptyList();
        }
    }

    /**
     * 添加连接
     *
     * @param connection 连接定义
     */
    public void addConnection(ConnectionSpec connection) {
        if (connections == null) {
            connections = new ArrayList<>(4);
        }
        connections.add(connection);
    }

//    /**
//     * 从服务器定义中扩展
//     *
//     * @param server 服务器定义
//     */
//    public void extend(ServerSpec server) {
//        if (port == 0) {
//            setPort(server.getPort());
//        }
//        //Connections
//        List<ConnectionSpec> conns = server.getConnections();
//        if (!conns.isEmpty()) {
//            ArrayList<ConnectionSpec> list = new ArrayList<>(conns.size() + (connections != null ? connections.size() : 0));
//            list.addAll(conns);
//            if (connections != null) {
//                list.addAll(connections);
//            }
//            connections = list;
//        }
//    }

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

    /**
     * 实际真正初始化
     */
    public void initialize() {
        if (type == null) {
            throw new InitializeException("No 'type' attribute of a <server>");
        }
    }

    public void setConnections(List<ConnectionSpec> connections) {
        this.connections = connections;
    }
}
