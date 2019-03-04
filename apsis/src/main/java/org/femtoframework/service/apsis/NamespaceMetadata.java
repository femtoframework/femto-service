package org.femtoframework.service.apsis;


import org.femtoframework.bean.NamedBean;

import java.util.Collection;

/**
 * Apsis Service Metadata，服务的相关信息
 *
 * @author fengyun
 * @version 1.00 2005-11-18 16:45:47
 */
public interface NamespaceMetadata extends NamedBean
{
    /**
     * 返回模块名称集合
     *
     * @return 模块名称集合
     */
    Collection<String> getComponentNames();

    /**
     * 根据名称返回Model的Metadata
     *
     * @param name 组件名称
     * @return ComponentMetadata
     */
    ComponentMetadata getComponentMetadata(String name);
}
