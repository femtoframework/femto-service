package org.femtoframework.cube.spec;

import org.femtoframework.bean.Nameable;
import org.femtoframework.bean.NamedBean;
import org.femtoframework.cube.CubeConstants;
import org.femtoframework.cube.CubeUtil;
import org.femtoframework.net.InetAddressUtil;
import org.femtoframework.parameters.Parameters;
import org.femtoframework.util.StringUtil;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * 系统定义
 *
 * @author fengyun
 * @version 1.00 2005-3-1 16:29:01
 */
public class SystemSpec implements NamedBean, Nameable {

    private String name;

    /**
     * 内部标识符
     */
    private String id;

//    private List<HostSpec> hosts = new ArrayList<>();
    private List<ServerSpec> servers = new ArrayList<>();

    private HostSpec host = null;
    private ServerSpec server = null;

    private String version = CubeConstants.CUBE_V_7;

    private Platform platform = Platform.STATIC;

    /**
     * 返回对象名称
     *
     * @return 对象名称
     */
    public String getName() {
        if (name == null) {
            name = getId();
        }
        return name;
    }

    /**
     * 返回系统标示符，为了能够标识在一个网络内的不同系统，而产生的一个标识。
     * 如果系统没有设置名称，那么返回的是这个标识
     *
     * @return 系统标识
     */
    public String getId() {
        if (id == null) {
            id = "cube_" + getInternalId();
        }
        return id;
    }

    /**
     * 返回内部标识号，根据局域网内目前所有的非127.0.0.1的IP地址的最后一位之和，除以256之后获得的数字
     *
     * @return 内部标识号
     */
    private int getInternalId() {
        int c = 0;
        InetAddress localAddress = InetAddressUtil.getLocalAddress();
//        if (!hosts.isEmpty()) {
//            Iterator<HostSpec> values = hosts.iterator();
//            String address;
//            while (values.hasNext()) {
//                HostSpec def = values.next();
//                address = def.getAddress();
//                if (address == null) {
//                    address = localAddress.getHostAddress();
//                }
//                else {
//                    //如果是127.0.0.1，则采用主机名对应的IP地址作为因子
//                    if ("127.0.0.1".equals(address) && localAddress.getHostName().equals(def.getName())) {
//                        address = localAddress.getHostAddress();
//                    }
//                }
//                c += getIdFromAddress(address);
//            }
//        }
//        else {
            c = getIdFromAddress(localAddress.getHostAddress());
//        }
        return c & 0xFF;
    }

    private int getIdFromAddress(String address) {
        int i = address.lastIndexOf('.');
        return Integer.parseInt(address.substring(i + 1));
    }

//    /**
//     * 返回所有的主机
//     *
//     * @return 所有的主机
//     */
//    public List<HostSpec> getHosts() {
//        return hosts;
//    }

//    /**
//     * 返回所有拥有相应服务器类型的主机
//     *
//     * @return 所有的主机
//     */
//    public List<HostSpec> getHostsByServer(String server) {
//        List<HostSpec> list = new ArrayList<>();
//        for (HostSpec hostSpec: hosts) {
//            if (hostSpec.getServers().contains(server)) {
//                list.add(hostSpec);
//            }
//        }
//        return list;
//    }

//    /**
//     * 添加主机
//     *
//     * @param host 主机
//     */
//    public void addHost(HostSpec host) {
//        hosts.add(host);
//    }
//
//    /**
//     * 根据主机名返回主机
//     *
//     * @param name 主机名
//     * @return 主机
//     */
//    public HostSpec getHost(String name) {
//        for(HostSpec hostSpec: hosts) {
//            if (StringUtil.equals(hostSpec.getName(), name)) {
//                return hostSpec;
//            }
//        }
//        return null;
//    }

    /**
     * 添加服务器
     *
     * @param server 服务器
     */
    public void addServer(ServerSpec server) {
        servers.add(server);
    }

    /**
     * 返回所有的服务器定义
     *
     * @return 所有的服务器定义
     */
    public List<ServerSpec> getServers() {
        return servers;
    }

    /**
     * 根据服务器类型返回服务器定义
     *
     * @param type 服务器类型
     * @return 服务器定义
     */
    public ServerSpec getServer(String type) {
        for(ServerSpec serverSpec: servers) {
            if (StringUtil.equals(serverSpec.getType(), type)) {
                return serverSpec;
            }
        }
        return null;
    }

    /**
     * 返回当前服务器定义
     *
     * @return 服务器定义
     */
    public ServerSpec getCurrentServer() {
        if (server == null) {
            String serverType = CubeUtil.getServerType();
            server = getServer(serverType);
        }
        return server;
    }

    /**
     * 返回当前主机定义
     *
     * @return 主机定义
     */
    public HostSpec getCurrentHost() {
        if (host == null) {
            String hostName = System.getProperty("cube.system.host");
            if (hostName == null) {
                hostName = System.getenv("CUBE_SYSTEM_HOST");
                if (hostName == null) {
                    hostName = "localhost";
                }
            }

            String address = System.getProperty("cube.system.address");
            if (address == null) {
                address = System.getenv("CUBE_SYSTEM_ADDRESS");
                if (address == null) {
                    address  = "127.0.0.1";
                }
            }
            HostSpec host = new HostSpec();
            host.setName(hostName);
            host.setAddress(address);
            host.setServers(Collections.singletonList(getCurrentServer().getType()));
            //TODO
//            host = getHost(hostName);
            this.host = host;
        }
        return host;
    }

    /**
     * 设置系统标识符
     *
     * @param id 标识符
     */
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

//    public void setHosts(List<HostSpec> hosts) {
//        this.hosts = hosts;
//    }

    public void setServers(List<ServerSpec> servers) {
        this.servers = servers;
    }
}
