package org.femtoframework.service.apsis.coin;


import org.femtoframework.bean.Startable;
import org.femtoframework.coin.CoinUtil;
import org.femtoframework.coin.Namespace;
import org.femtoframework.coin.NamespaceFactory;
import org.femtoframework.coin.naming.CoinNamingParser;
import org.femtoframework.naming.NamingService;
import org.femtoframework.service.apsis.naming.AbstractNamingService;
import org.femtoframework.service.rmi.server.UnicastServerRef;

import javax.naming.*;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Coin名字服务实现
 *
 * @author fengyun
 * @version 1.00 2005-6-3 16:45:01
 */
public class CoinNamingService extends AbstractNamingService
    implements Remote, Startable
{
    private NamespaceFactory namespaceFactory;

    public CoinNamingService()
    {
        this.parser = new CoinNamingParser();
        try {
            prefix = parser.parse("");
        }
        catch (NamingException e) {
        }
        namespaceFactory = CoinUtil.getModule().getNamespaceFactory();
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
        String namespace;
        Name n;
        if (name.size() < 2) {
            namespace = "java";
            n = name;
        }
        else {
            namespace = name.get(0);
            n = name.getSuffix(1);
        }
        NamingService service = getService(namespace, true);
        service.bind(n, obj, className);
    }

    /**
     * 取消邦定给定的名字
     *
     * @param name 名字
     * @throws NamingException 命名异常
     */
    public void unbind(Name name) throws NamingException
    {
        if (name.size() < 2) {
            return;
        }
        String namespace = name.get(0);
        NamingService service = getService(namespace, false);
        if (service != null) {
            service.unbind(name.getSuffix(1));
        }
    }

    /**
     * 启动服务
     */
    public void start()
    {
        setup(new UnicastServerRef(CoinNamingConstants.OBJ_ID));
    }

    /*
     * Create the export the object using the parameter
     * <code>uref</code>
     */
    private void setup(UnicastServerRef uref)
    {
        /* Server ref must be created and assigned before remote
         * object 'this' can be exported.
         */
        ref = uref;
        uref.exportObject(this, null);
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
            result = new CoinNamingContext(null, prefix, this);
        }
        else if (name.size() > 1) {
            // Recurse to find correct context
            String first = name.get(0);
            NamingService ctx = getService(first);
            if (ctx != null) {
                result = ctx.lookup(name.getSuffix(1));
            }
            else {
                throw new NotContextException();
            }
        }
        else {
            // Get object to return
            String first = name.get(0);
            if (first.length() == 0) {
                result = new CoinNamingContext(null, prefix, this);
            }
            else {
                NamingService service = getService(first);
                if (service != null) {
                    result = new CoinNamingContext(null, name, this);
                }
                else {
                    throw new NotContextException();
                }
            }
        }

        return result;
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
        if (name.isEmpty()) {
            ArrayList<NameClassPair> list = new ArrayList<NameClassPair>();
            for (String key : namespaceFactory.getNames()) {
                Namespace obj = namespaceFactory.get(key);
                if (obj != null) {
                    list.add(new NameClassPair(key, obj.getClass().getName(), true));
                }
            }
            return list;
        }
        else if (name.size() == 1) {
            String objectName = name.get(0);
            Namespace ns = namespaceFactory.get(objectName);
            if (ns != null) {
                List<NameClassPair> list = new ArrayList<NameClassPair>(1);
                list.add(new NameClassPair(objectName, ns.getClass().getName(), true));
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

    protected NamingService getService(String objectName)
    {
        return getService(objectName, false);
    }

    protected NamingService getService(String objectName, boolean create)
    {
        Binding binding = getBinding(objectName, create);
        return (NamingService)(binding != null ? binding.getObject() : null);
    }

    protected Binding getBinding(String objectName, boolean create)
    {
        Binding binding = bindings.get(objectName);
        if (binding == null) {
            synchronized (bindings) {
                binding = bindings.get(objectName);
                if (binding == null) {
                    Namespace ns = namespaceFactory.getNamespace(objectName, create);
                    if (ns == null) {
                        return null;
                    }
                    //自动Export
                    Object service = new NamespaceService(objectName, ns, this);
                    binding = new Binding(objectName, service, true);
                    bindings.put(objectName, binding);
                }
            }
        }
        return binding;
    }

    protected Binding getBinding(String objectName)
    {
        return getBinding(objectName, false);
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
        if (name.size() == 1) {
            String serviceName = name.get(0);
            NamingService service = getService(serviceName, true);
            if (service != null) {
                return new CoinNamingContext(null, name, this);
            }
            else {
                throw new NotContextException();
            }
        }
        else {
            throw new OperationNotSupportedException("Please give a one size name:" + name);
        }
    }
}
