package org.femtoframework.service.apsis;

import java.net.URI;

/**
 * 客户端工厂
 * 根据scheme创建客户端
 *
 * @author fengyun
 * @version 1.00 2005-6-4 23:22:14
 */
public interface ApsisClientFactory
{
    /**
     * 创建客户端
     *
     * @param uri URI地址
     * @return 客户端对象仅仅执行到配置的步骤，不调用Lifecycle相关的方法
     */
    ApsisClient createClient(URI uri);
}
