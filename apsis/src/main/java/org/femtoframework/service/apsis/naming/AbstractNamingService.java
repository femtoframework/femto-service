package org.femtoframework.service.apsis.naming;

import org.femtoframework.naming.NamingService;
import org.femtoframework.service.rmi.server.RemoteObject;

import javax.naming.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Coin名字服务的抽象实现
 *
 * @author fengyun
 * @version 1.00 2005-6-4 20:25:20
 */
public abstract class AbstractNamingService
    extends RemoteObject
    implements NamingService
{
    protected NameParser parser;

    protected Name prefix;

    protected final HashMap<String, Binding> bindings = new HashMap<>();

    /**
     * 用给定的名字邦定对应的对象
     *
     * @param name      名字
     * @param obj       对象
     * @param className 类名
     * @throws NamingException 命名异常
     */
    public void bind(Name name, Object obj, String className) throws NamingException
    {
        //throw new OperationNotSupportedException("Can't bind to coin");
    }

    /**
     * 用给定的名字重新邦定对应的对象
     *
     * @param name      名字
     * @param obj       对象
     * @param className 类名
     * @throws NamingException 命名异常
     */
    public void rebind(Name name, Object obj, String className) throws NamingException
    {
        unbind(name);
        bind(name, obj, className);
    }

    /**
     * 取消邦定给定的名字
     *
     * @param name 名字
     * @throws NamingException 命名异常
     */
    public void unbind(Name name) throws NamingException
    {
//        throw new OperationNotSupportedException("Can't unbind from coin");
    }

    protected abstract Binding getBinding(String objectName);

    /**
     * 列出符合给定名字的所有邦定对象
     *
     * @param name 名字
     * @return
     * @throws NamingException 命名异常
     */
    public Collection<Binding> listBindings(Name name) throws NamingException
    {
        if (name.isEmpty()) {
            return bindings.values();
        }
        else if (name.size() == 1) {
            String objectName = name.get(0);
            Binding binding = getBinding(objectName);
            if (binding != null) {
                List<Binding> list = new ArrayList<>(1);
                list.add(binding);
                return list;
            }
            else {
                return null;
            }
        }
        else {
            throw new NotContextException();
        }
    }
}
