package org.femtoframework.service.apsis;

import org.femtoframework.bean.NamedBean;

import java.util.Collection;

/**
 * Apsis Server Metadata
 *
 * @author fengyun
 * @version 1.00 2005-11-18 16:48:40
 */
public interface ServerMetadata extends NamedBean
{
    /**
     * 返回服务器应用类型
     *
     * @return 服务器应用类型
     */
    String getType();

    /**
     * 返回服务器标识
     *
     * @return 服务器标识
     */
    long getId();

    /**
     * 返回模块名称集合
     *
     * @return 模块名称集合
     */
    Collection<String> getServiceNames();

    /**
     * 根据名称返回Model的Metadata
     *
     * @param name 组件名称
     * @return ComponentMetadata
     */
    NamespaceMetadata getServiceMetadata(String name);
}
