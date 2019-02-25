package org.femtoframework.cube;

import org.femtoframework.cube.spec.HostSpec;
import org.femtoframework.cube.spec.ServerSpec;
import org.femtoframework.cube.spec.SystemSpec;
import org.femtoframework.implement.ImplementUtil;
import org.femtoframework.net.socket.bifurcation.BifurcationSocketHandlerFactory;

import java.util.List;

/**
 * Cube工具类
 *
 * @author fengyun
 * @version 1.00 2005-3-10 0:01:10
 */
public class CubeUtil
{
    /**
     * CubeManager
     */
    private static CubeModule module;

    /**
     * 返回Cube模块
     *
     * @return Cube模块
     */
    public static CubeModule getModule()
    {
        if (module == null) {
            module = ImplementUtil.getInstance(CubeModule.class);
        }
        return module;
    }

    /**
     * 返回SocketHandlerFactory
     *
     * @return SocketHandlerFactory
     */
    public static BifurcationSocketHandlerFactory getSocketHandlerFactory()
    {
        return getModule().getSocketHandlerFactory();
    }


    /**
     * 返回系统定义
     *
     * @return 系统定义
     */
    public static SystemSpec getSystemSpec()
    {
        return getModule().getSystemSpec();
    }

    /**
     * 返回系统标示符
     *
     * @return 系统标识
     */
    public static String getSystemId()
    {
        return getSystemSpec().getId();
    }

    /**
     * 返回所有的主机
     *
     * @return 所有的主机
     */
    public static List<HostSpec> getHosts()
    {
        return getSystemSpec().getHosts();
    }

    /**
     * 根据主机名返回主机
     *
     * @param name 主机名
     * @return 主机
     */
    public static HostSpec getHost(String name)
    {
        return getSystemSpec().getHost(name);
    }

    /**
     * 返回所有的服务器定义
     *
     * @return 所有的服务器定义
     */
    public static List<ServerSpec> getServers()
    {
        return getSystemSpec().getServers();
    }

    /**
     * 根据服务器类型返回服务器定义
     *
     * @param type 服务器类型
     * @return 服务器定义
     */
    public static ServerSpec getServer(String type)
    {
        return getSystemSpec().getServer(type);
    }

    /**
     * 根据服务器类型返回服务器定义
     *
     * @return 服务器定义
     */
    public static ServerSpec getCurrentServer()
    {
        return getSystemSpec().getCurrentServer();
    }

    /**
     * 根据服务器类型返回服务器定义
     *
     * @return 服务器定义
     */
    public static HostSpec getCurrentHost()
    {
        return getSystemSpec().getCurrentHost();
    }

    /**
     * 返回当前的AppServer
     *
     * @return 当前的应用服务器
     */
    public static AppServer getAppServer()
    {
        return getModule().getAppServer();
    }
}
