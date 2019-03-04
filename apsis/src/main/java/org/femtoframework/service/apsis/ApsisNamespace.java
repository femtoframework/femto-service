package org.femtoframework.service.apsis;


import org.femtoframework.coin.Namespace;

/**
 * Apsis服务是管理不同类型的Model组件的，<br>
 * 它允许相同类型的组件组成一个统一的集合，供外部应用程序使用
 *
 * @author fengyun
 * @version 1.00 2005-6-12 23:20:57
 */
public interface ApsisNamespace extends Namespace
{
    /**
     * 返回服务Metadata
     *
     * @return 服务Metadata
     */
    NamespaceMetadata getMetadata();
}
