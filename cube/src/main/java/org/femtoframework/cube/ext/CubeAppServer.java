package org.femtoframework.cube.ext;

import org.femtoframework.cube.AppServer;
import org.femtoframework.cube.Endpoint;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Cube App Server实现
 *
 * @author fengyun
 * @version 1.00 2005-3-10 1:26:19
 */

public class CubeAppServer implements AppServer
{

    private Map<String, Endpoint> endpoints = new HashMap<>();
    private Map<String, Object> servers = new HashMap<>();

    /**
     * 返回所有的接入点的名称
     */
    public Collection<String> getEndpointNames() {
        return endpoints.keySet();
    }

    /**
     * 根据名称返回接入点
     *
     * @param name 名称
     * @return 如果存在返回相应的接入点，否则返回<code>null</code>
     */
    public Endpoint getEndpoint(String name) {
        return endpoints.get(name);
    }

    /**
     * 添加接入点
     *
     * @param endpoint 接入点
     */
    public void addEndpoint(Endpoint endpoint) {
        endpoints.put(endpoint.getName(), endpoint);
    }

    /**
     * 返回所有的服务器的名称
     */
    public Collection<String> getServerNames() {
        return servers.keySet();
    }

    /**
     * 根据名称返回服务器
     *
     * @param name 名称
     * @return 如果存在返回相应的服务器，否则返回<code>null</code>
     */
    public Object getServer(String name) {
        return servers.get(name);
    }

    /**
     * 添加服务器
     *
     * @param name   服务器名称
     * @param server 服务器
     */
    public void addServer(String name, Object server) {
        if (name == null) {
            throw new IllegalArgumentException("No name of the server:" + server);
        }
        servers.put(name, server);
    }
}
