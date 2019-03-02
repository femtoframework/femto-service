package org.femtoframework.cube;

/**
 * Cube常量定义
 *
 * @author fengyun
 * @version 1.00 2005-3-12 3:08:51
 */
public interface CubeConstants
{
    String CUBE = "cube";
    String SPEC_VERSION = "cube/v7";
    String SYSTEM = "system";
    String HOST = "host";
    String SERVER = "server";
    String BACKEND = "backend";
    String APP_SERVER = "app_server";
    String TCP_ENDPOINT = "tcp_endpoint";

    int BIFURCATION_STOP = 0x44;

    int BIFURCATION_STATUS = 0x45;

    /**
     * 默认通讯端口
     */
    int DEFAULT_TCP_PORT = 9168;
}
