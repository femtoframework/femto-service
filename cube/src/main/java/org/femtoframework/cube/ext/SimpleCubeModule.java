package org.femtoframework.cube.ext;

import org.femtoframework.coin.*;
import org.femtoframework.cube.AppServer;
import org.femtoframework.cube.CubeConstants;
import org.femtoframework.cube.CubeModule;
import org.femtoframework.cube.spec.SystemSpec;
import org.femtoframework.net.socket.bifurcation.BifurcationSocketHandlerFactory;

/**
 * Cube Manager 简单实现
 *
 * @author fengyun
 * @version 1.00 2005-3-16 0:06:59
 */
public class SimpleCubeModule implements CubeModule
{

    private BifurcationSocketHandlerFactory socketHandlerFactory = new CubeSocketHandlerFactory();

    /**
     * AppServer实例
     */
    private AppServer appServer;

    private SystemSpec systemSpec;

    /**
     * 返回系统定义
     *
     * @return 系统定义
     */
    public SystemSpec getSystemSpec() {
        if (systemSpec != null) {
            return systemSpec;
        }
        synchronized (this) {
            if (systemSpec == null) {
                CoinModule coinModule = CoinUtil.getModule();
                Namespace namespace = coinModule.getNamespaceFactory().getNamespace(CubeConstants.CUBE, true);
                ComponentFactory componentFactory = namespace.getComponentFactory();
                Component component = componentFactory.get(CubeConstants.NAME_SYSTEM);
                if (component == null) {
                    throw new IllegalStateException("No such component, cube:" + CubeConstants.NAME_SYSTEM);
                }
                systemSpec = (SystemSpec)component.getBean();
            }
        }
        return systemSpec;
    }

    /**
     * 返回当前的AppServer
     *
     * @return 当前的应用服务器
     */
    public AppServer getAppServer() {
        if (appServer != null) {
            return appServer;
        }
        synchronized (this) {
            if (appServer == null) {
                CoinModule coinModule = CoinUtil.getModule();
                Namespace namespace = coinModule.getNamespaceFactory().getNamespace(CubeConstants.CUBE, true);
                ComponentFactory componentFactory = namespace.getComponentFactory();
                Component component = componentFactory.get(CubeConstants.NAME_APP_SERVER);
                if (component == null) {
                    throw new IllegalStateException("No such component, cube:" + CubeConstants.NAME_APP_SERVER);
                }
                appServer = (AppServer)component.getBean();
            }
        }
        return appServer;
    }

    /**
     * 返回SocketHandlerFactory
     *
     * @return SocketHandlerFactory
     */
    public BifurcationSocketHandlerFactory getSocketHandlerFactory() {
        return socketHandlerFactory;
    }

    public void setSocketHandlerFactory(BifurcationSocketHandlerFactory socketHandlerFactory) {
        this.socketHandlerFactory = socketHandlerFactory;
    }
}
