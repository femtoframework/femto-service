package org.femtoframework.service.apsis;

import org.femtoframework.implement.ImplementUtil;
/**
 * Apsis Server Util
 *
 * @author fengyun
 * @version 1.00 2005-9-19 20:39:56
 */
public class ApsisServerUtil {
    private static ApsisServer server;

//    private static ApsisServiceTree serviceTree;

    public static ApsisServer getServer() {
        if (server == null) {
            server = ImplementUtil.getInstance(ApsisServer.class);
        }
        return server;
    }

//    /**
//     * 返回服务容器
//     *
//     * @return 服务容器
//     */
//    public static ServiceContainer<ApsisService> getServiceContainer() {
//        return SimpleServiceContainer.INSTANCE;
//    }
//
//    /**
//     * 返回Session容器
//     *
//     * @return Session容器
//     */
//    public static SessionContainer<ApsisSession> getSessionContainer() {
//        return SimpleSessionContainer.getInstance();
//    }

//    /**
//     * create a session context
//     *
//     * @param serviceName
//     * @param args
//     * @return
//     */
//    public static SessionContext createSessionContext(String serviceName, Arguments args) {
//        return new SimpleSessionContext(getSessionContainer(), args, serviceName);
//    }
}