package org.femtoframework.cube;


import org.femtoframework.cube.spec.SystemSpec;
import org.femtoframework.net.socket.bifurcation.BifurcationSocketHandlerFactory;

/**
 * Cube相关部分管理器
 *
 * @author fengyun
 * @version 1.00 2005-3-16 0:01:19
 */
public interface CubeModule
{
    /**
     * 返回系统Spec
     */
    SystemSpec getSystemSpec();

    /**
     * 返回当前的AppServer
     *
     * @return 当前的应用服务器
     */
    AppServer getAppServer();

    /**
     * 返回SocketHandlerFactory
     *
     * @return SocketHandlerFactory
     */
    BifurcationSocketHandlerFactory getSocketHandlerFactory();

}
