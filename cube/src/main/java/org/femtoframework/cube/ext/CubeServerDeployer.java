package org.femtoframework.cube.ext;

import org.femtoframework.coin.launcher.LauncherListener;
import org.femtoframework.cube.CubeModule;
import org.femtoframework.cube.CubeUtil;
import org.femtoframework.cube.spec.HostSpec;
import org.femtoframework.cube.spec.ServerSpec;
import org.femtoframework.cube.spec.SystemSpec;
import org.femtoframework.util.StringUtil;

import java.net.URI;
import java.util.List;

/**
 * Cube服务器部署工具
 *
 * @author fengyun
 * @version 1.00 2005-3-15 17:48:24
 */
public class CubeServerDeployer implements LauncherListener
{
//    /**
//     * 主机名
//     */
//    private String hostName;

    /**
     * 服务器类型
     */
    private String serverType;

    /**
     * System
     */
    private SystemSpec system;

    /**
     * 主机定义
     */
    private HostSpec host;

    /**
     * 服务器定义
     */
    private ServerSpec server;

    /**
     * 初始化实现
     */
    protected void initServer() {

        system = CubeUtil.getSystemSpec();
//        this.hostName = .getName();

        host = system.getCurrentHost();
//        if (host == null) {
//            host = system.getHost("localhost");
//            if (host == null) {
//                System.err.println("[CUBE]The host:" + hostName + " can't be found in the yaml.");
//                System.exit(0);
//            }
//        }

        server = system.getCurrentServer();
        if (server == null) {
            System.err.println("[CUBE]The server type:" + serverType + " can't be found in the yaml.");
            System.exit(0);
        }
    }


    private void initDefaultProperties() {
        String address = host.getAddress();
        System.setProperty("cube.system.address", address);
        System.setProperty("cube.system.addresses", StringUtil.toString(host.getAddresses(), ','));
        System.setProperty("cube.system.port", String.valueOf(server.getPort()));
    }

    /**
     * 实际启动实现
     */
    protected void startServer() {
        //启动AppServer
        CubeUtil.getAppServer();
    }

    /**
     * On Before Starting
     */
    @Override
    public void onBeforeStarting() {
        serverType = CubeUtil.getServerType();
        System.setProperty("cube.system.type", serverType);
    }

    /**
     * On Yaml Files Found
     *
     * @param yamlFiles Yaml Files
     * @return URI List
     */
    @Override
    public List<URI> onYamlFilesFound(List<URI> yamlFiles) {
        return yamlFiles;
    }

    /**
     * On After Loading Yaml Files
     *
     * @param yamlFiles Yaml Files
     */
    @Override
    public void onAfterLoadingYamlFiles(List<URI> yamlFiles) {
        initServer();

        initDefaultProperties();

        startServer();
    }
}
