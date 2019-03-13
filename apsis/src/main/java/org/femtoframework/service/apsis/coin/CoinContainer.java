package org.femtoframework.service.apsis.coin;

import org.femtoframework.coin.BeanFactory;
import org.femtoframework.coin.ComponentFactory;
import org.femtoframework.naming.NamingService;
import org.femtoframework.service.Container;

import java.util.Collection;

/**
 * COIN对象容器
 * <p/>
 * 命名解析
 * <p/>
 * 首先遵从COIN命名的基本原则 namespace:name 能够访问首层的对象
 * namespace 能够返回一个Context（目录）
 * 其次如果是首层对象派生出的对象（属性或者工厂的成员），那么采用下面的规则：
 * Property: namespace:name/property_object            对应的方法是 Object getPropertyObject
 * FactoryObject:  namespace:name/object(test)       对应的方法是 Object getObject(String)
 * 参数说明：如果类型是字符串(String)，其中存在':'，那么需要采用\:来替代
 * 如果是基础类型，那么直接采用相应的值，比如说 123456 true
 * 对应的方法分别是 getObject(int) 和 getObject(boolean)，根据方法上的参数类型来造型
 * 如果是Enum的枚举类型，那么采用'enum='+类名+常量来表示： enum=org.bolango.frame.comm.ConnectionMode.READ_ONLY
 * 如果是类，那么直接采用'class=' + 类名： class=org.bolango.frame.comm.ConnectionMode
 * 如果是常量，那么采用'const=' + 类名 + 常量名来表示：const=org.bolango.util.MessageLocale.ZH_CN
 * 不能是其它对象（那样最好不要在名称上体现）
 * <p/>
 * 再次是采用这种方式来递归出下一层的对象，不同层次的对象采用'/'分隔
 * <p/>
 * 下面是例子：
 * <p/>
 * apsis                                           --> ObjectFactory
 * apsis:server                                    --> SimpleApsisServer
 * apsis:server/server(rmi)                        --> RmiServer
 * apsis:server/connector(gmpp)                    --> GmppConnector
 * apsis:server/server(cmd)                        --> CommandServer
 * apsis:server/server(cmd)/service(demo)          --> CommandServer / Service(demo)
 *
 * @author fengyun
 * @version 1.00 2005-6-5 4:10:42
 */
public interface CoinContainer extends Container {
    /**
     * 根据对象名称返回对象
     *
     * @param name 名称(可以采用上面的模式规则）
     * @return
     */
    Object getObject(String name);

    /**
     * 返回所有的名字空间
     *
     * @return 名字空间集合
     */
    Collection<String> getNamespaces();

    /**
     * 根据名字空间返回对象工厂
     *
     * @return 对象工厂，如果找不到，返回<code>null</code>
     */
    BeanFactory getBeanFactory(String namespace);

    /**
     * 返回命名服务
     *
     * @return 命名服务
     */
    NamingService getNamingService();
}
