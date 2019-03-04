package org.femtoframework.service.apsis;

import org.femtoframework.bean.Destroyable;
import org.femtoframework.service.Session;

/**
 * Apsis会话，其中包含一个服务器会话，用于服务间的数据共享，
 * 每个服务拥有独立的变量空间
 *
 * @author fengyun
 * @version 1.00 2005-7-6 1:42:33
 * @see Session
 */
public interface ApsisSession extends Session, Destroyable
{
    /**
     * 根据服务名称返回相应的会话，服务会话不可以拥有自己单独的
     *
     * @param namespace Namespace
     * @return Namespace Session
     */
    Session getNamespaceSession(String namespace);

    /**
     * 根据服务名称返回相应的会话，服务会话不可以拥有自己单独的
     *
     * @param namespace Namespace
     * @param create      如果服务不存在，是否创建相应的服务会话
     * @return 服务会话
     */
    Session getNamespaceSession(String namespace, boolean create);

    /**
     * stop a namespace session
     *
     * @param namespace Namespace
     */
    void endNamespaceSession(String namespace);
}
