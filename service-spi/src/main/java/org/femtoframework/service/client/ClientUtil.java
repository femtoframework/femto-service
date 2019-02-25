package org.femtoframework.service.client;

import org.femtoframework.implement.ImplementUtil;
import org.femtoframework.service.Client;

import java.net.URI;

/**
 * 客户端工具类
 *
 * @author fengyun
 */
public class ClientUtil {

    private static ClientManager manager = ImplementUtil.getInstance(ClientManager.class);


    public static <M extends ClientManager> M getManager() {
        return (M)manager;
    }

    public static <B extends Balancer> B getBalancer() {
        return (B)manager.getBalancer();
    }

    /**
     * 根据客户端标识返回客户端
     *
     * @param id     客户端标识
     * @param create 如果不存在，是否自动创建
     */
    public static <C extends Client> C getClient(long id, boolean create) {
        return (C)manager.getClient(id, create);
    }

    /**
     * 根据主机地址和端口返回客户端
     *
     * @param host   主机
     * @param port   端口
     * @param create 如果不存在是否自动创建
     */
    public static <C extends Client> C getClient(String host, int port, boolean create) {
        return (C)manager.getClient(host, port, create);
    }

    /**
     * 创建客户端
     *
     * @param uri URI地址
     * @return 客户端对象仅仅执行到配置的步骤，不调用Lifecycle相关的方法
     */
    public static <C extends Client> C createClient(URI uri) {
        return (C)manager.createClient(uri);
    }

    /**
     * 获取指定类型服务器的数量
     * 指已经连接到的，如果连接失败将不计数
     *
     * @param appType 应用类型
     * @return 数量
     */
    public static int getClientCount(String appType) {
        return manager.getClientCount(appType);
    }
}
