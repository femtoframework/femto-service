package org.femtoframework.service.apsis.coin;

import org.femtoframework.coin.*;
import org.femtoframework.coin.naming.CoinName;
import org.femtoframework.coin.naming.CoinNamingParser;
import org.femtoframework.naming.NamingContext;
import org.femtoframework.naming.NamingService;
import org.femtoframework.service.apsis.naming.AbstractNamingService;
import org.femtoframework.service.rmi.RmiUtil;
import org.femtoframework.text.NamingConvention;

import javax.naming.*;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Naming Service for COIN Namespace
 *
 * @author fengyun
 * @version 1.00 2005-6-4 20:24:43
 */
class NamespaceService extends AbstractNamingService
    implements Remote
{
    private Namespace namespace;

    private NamingService parent = null;

    private CoinLookup coinLookup = null;

    public NamespaceService(String namespace, Namespace ns, NamingService parent)
    {
        this.parser = new CoinNamingParser();
        try {
            prefix = parser.parse(namespace);
        }
        catch (NamingException e) {
        }
        this.namespace = ns;
        this.parent = parent;
        this.coinLookup = CoinUtil.getModule().getLookup();
    }

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
        if (name.size() != 1) {
            throw new OperationNotSupportedException("Can't bind to coin, name:" + name);
        }
        String n = name.get(0);
        namespace.getBeanFactory().add(n, obj);
    }

    /**
     * 取消邦定给定的名字
     *
     * @param name 名字
     * @throws NamingException 命名异常
     */
    public void unbind(Name name) throws NamingException
    {
        if (name.size() != 1) {
            throw new OperationNotSupportedException("Can't bind to coin, name:" + name);
        }
        String n = name.get(0);
        namespace.getBeanFactory().delete(n);
    }

    /**
     * 查找给定名字的对象
     *
     * @param name 名字
     * @return
     * @throws NamingException 命名异常
     */
    public Object lookup(Name name) throws NamingException
    {
        Object result;
        if (name.isEmpty()) {
            // Return this
            result = new NamingContext(null, prefix, parent);
        }
        else if (name.size() > 1) {
            result = coinLookup.lookup(ResourceType.BEAN, namespace, name);
        }
        else {
            //第一层的业务对象自动导出
            String objectName = name.get(0);
            result = getObject(objectName);
        }

        return result;
    }

    protected Object getObject(String objectName)
    {
        Binding binding = getBinding(objectName);
        return binding != null ? binding.getObject() : null;
    }

    private String toUri(String objectName)
    {
        String namespace = this.namespace.getName();
        StringBuilder sb = new StringBuilder(namespace.length() + objectName.length() + 1);
        sb.append(namespace);
        sb.append('/');
        sb.append(objectName);
        return sb.toString();
    }

    protected Binding getBinding(String objectName)
    {
        Binding binding = bindings.get(objectName);
        if (binding == null) {
            synchronized (bindings) {
                binding = bindings.get(objectName);
                if (binding == null) {
                    //自动名称格式化
                    String name = NamingConvention.format(objectName);
                    Object result = namespace.getBeanFactory().get(name);
                    if (result == null) {
                        return null;
                    }
                    //自动Export
                    if (!(result instanceof Reference)) {
                        result = RmiUtil.exportObject(result, toUri(objectName));
                    }
                    binding = new Binding(objectName, result, true);
                    bindings.put(objectName, binding);
                }
            }
        }
        return binding;
    }

    /**
     * 列出符合给定名字的所有对象
     *
     * @param name 名字
     * @return
     * @throws NamingException 命名异常
     */
    public Collection<NameClassPair> list(Name name) throws NamingException
    {
        BeanFactory beanFactory = namespace.getBeanFactory();

        if (name.isEmpty()) {
            List<NameClassPair> list = new ArrayList<>();
            for (String key : beanFactory.getNames()) {
                Object obj = beanFactory.get(key);
                list.add(new NameClassPair(key, obj.getClass().getName(), true));
            }
            return list;
        }
        else if (name.size() == 1) {
            String objectName = name.get(0);
            Object obj = beanFactory.get(objectName);
            if (obj != null) {
                List<NameClassPair> list = new ArrayList<>(1);
                list.add(new NameClassPair(objectName, obj.getClass().getName(), true));
                return list;
            }
            else {
                throw new NotContextException();
            }
        }
        else {
            throw new NotContextException();
        }
    }


    /**
     * 创建子上下文
     *
     * @param name 名字
     * @return
     * @throws NamingException 命名异常
     */
    public Context createSubcontext(Name name) throws NamingException
    {
        throw new OperationNotSupportedException();
    }
}
