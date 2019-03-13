package org.femtoframework.service.apsis.coin;

import org.bolango.bean.info.Property;
import org.bolango.coin.CoinUtil;
import org.bolango.coin.ObjectFactory;
import org.bolango.jade.Initable;
import org.bolango.naming.NamingService;
import org.bolango.naming.NamingUtil;
import org.bolango.service.Namable;

import javax.annotation.ManagedBean;
import java.util.Collection;

/**
 * 简单Coin容器实现
 *
 * @author fengyun
 * @version 1.00 2005-6-5 18:01:08
 */
@ManagedBean
public class SimpleCoinContainer
    implements CoinContainer, Initable, Namable {
    private String name;
    private NamingService localService;

    /**
     * 初始化
     */
    public void init() {
        localService = NamingUtil.getLocalService();
    }

    /**
     * 根据对象名称返回对象
     *
     * @param name 名称(可以采用上面的模式规则）
     * @return 对象
     */
    public Object getObject(String name) {
        return CoinUtil.lookup(name);
    }

    /**
     * 返回所有的名字空间
     *
     * @return 名字空间集合
     */
    @Property (name = "namespaces", type = "java.util.Collection", writable = false,
        desc = "All the namespace of the object factories")
    public Collection getNamespaces() {
        return CoinUtil.getObjectFactoryManager().getNamespaces();
    }

    /**
     * 根据名字空间返回对象工厂
     *
     * @return 对象工厂，如果找不到，返回<code>null</code>
     */
    public ObjectFactory getBeanFactory(String namespace) {
        return CoinUtil.getObjectFactory(namespace);
    }

    /**
     * 返回命名服务
     *
     * @return 命名服务
     */
    @Property (name = "namingService", type = "org.bolango.naming.NamingService", writable = false,
        desc = "The naming service of the coin container")
    public NamingService getNamingService() {
        return localService;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
