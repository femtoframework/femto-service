package org.femtoframework.service.apsis.event;

import org.femtoframework.bean.annotation.Property;
import org.femtoframework.coin.Namespace;
import org.femtoframework.pattern.Loggable;
import org.femtoframework.service.*;
import org.femtoframework.service.event.GenericEvent;
import org.femtoframework.service.event.GenericEventHandler;
import org.slf4j.Logger;

import javax.naming.Name;
import java.util.Collection;
import java.util.HashMap;

/**
 * 事件服务，没有动态过滤机制
 *
 * @author fengyun
 * @version 1.00 2005-9-19 22:44:43
 */
public class EventService extends EventContainerBase
    implements Loggable
{

    private Logger log;

//    /**
//     * 需要添加到listener上的拦截机
//     */
//    private ListenerLoadListener listenerLoadListener = null;

    /**
     * 命令映射
     */
    private final HashMap<String, Object> listeners = new HashMap<String, Object>();

    /**
     * Namespace
     */
    private Namespace namespace;

    /**
     * 构造
     *
     * @param name    名称
     * @param namespace 服务
     */
    public EventService(String name, Namespace namespace)
    {
        setName(name);
        setNamespace(namespace);
    }

    /**
     * 根据名称返回Model组件
     *
     * @param name 组件名称
     * @return Model组件
     */
    public Object getHandler(String name)
    {
        Object handler = listeners.get(name);
        if (handler == null) {
            synchronized (listeners) {
                handler = listeners.get(name);
                if (handler == null) {
                    handler = createHandler(name);
                }
                if (handler != null) {
                    listeners.put(name, handler);
                }
            }
        }
        return handler;
    }

    /**
     * 根据名称返回Model组件
     *
     * @param name 组件名称
     * @return Model组件
     */
    public EventValve getValve(String name)
    {
        Object model = namespace.getBeanFactory().get(name);
        if (model instanceof EventValve) {
            return (EventValve) model;
        }
        else {
            log.warn("The model is not a valid valve:" + model + " name:" + name);
        }
        return null;
    }

    /**
     * 创建组件
     *
     * @param name
     * @return 组件
     */
    protected Object createHandler(String name)
    {
        Object handler = namespace.getBeanFactory().get(name);
        if (handler == null) {
            return null;
        }
        else if (handler instanceof EventHandler || handler instanceof GenericEventHandler) {
            return handler;
        }
        else {
            return wrap(name, handler);
        }
    }

    protected EventHandler wrap(String name, Object command)
    {
        if (!ListenerWrapper.isListener(command)) {
            log.warn("The model is not a valid event:" + command + " name:" + name);
            return null;
        }

        ListenerWrapper wrapper = ListenerWrapper.createWrapper(name, command);

//        if (listenerLoadListener != null) {
//            listenerLoadListener.onLoad(getName(), name, command, wrapper);
//        }
        return wrapper;
    }

    /**
     * 返回所有组件的名称
     *
     * @return 所有组件的名称
     */
    @Property(writable = false)
    public Collection getListenerNames()
    {
        return listeners.keySet();
    }

    /**
     * 根据名称删除组件
     *
     * @param name 组件名称
     */
//    @Function
    public boolean removeListener(String name)
    {
        return listeners.remove(name) != null;
    }

    /**
     * 是否存在组件
     *
     * @param name 组件名字
     */
//    @Function
    public boolean hasListener(String name)
    {
        return listeners.containsKey(name) || namespace.getComponentFactory().get(name) != null;
    }

    /**
     * 添加组件
     *
     * @param name     组件名称
     * @param listener 组件
     */
//    @Function
    public void addListener(String name, Object listener)
    {
        Object wrapped;
        if (!(listener instanceof EventHandler)) {
            wrapped = wrap(name, listener);
        }
        else {
            wrapped = listener;
        }
        listeners.put(name, wrapped);
    }

//
//    /**
//     * 初始化实现
//     */
//    public void _doInit()
//    {
//        super._doInit();
////        initValves();
//    }

//    /**
//     * 初始化Valves
//     */
//    protected void initValves()
//    {
//        ApsisAttribute[] attrs = namespace.getAttributes(EventValveAttribute.class);
//        if (attrs.length > 0) {
//            for (int i = 0, len = attrs.length; i < len; i++) {
//                try {
//                    addValve(((EventValveAttribute) attrs[i]).getValve(namespace));
//                }
//                catch (Exception ex) {
//                    log.warn("Create static filter exception:", ex);
//                }
//            }
//        }
//    }
//
//    protected void initLoadListeners()
//    {
//        ApsisAttribute[] attrs = namespace.getAttributes(ListenerLoadListenerAttribute.class);
//        if (attrs.length > 0) {
//            for (int i = 0, len = attrs.length; i < len; i++) {
//                try {
//                    String className = ((ListenerLoadListenerAttribute) attrs[i]).getName();
//                    if (className == null) {
//                        log.warn("No 'name' of the command load listener:" + attrs[i]);
//                        continue;
//                    }
//                    addListenerLoadListener((ListenerLoadListener) Reflection.newInstance(className));
//                }
//                catch (Exception ex) {
//                    log.warn("Create command load listener exception:", ex);
//                }
//            }
//        }
//    }
//
//    /**
//     * 添加ListenerLoadListener
//     *
//     * @param listener 命令装载桢听者
//     */
//    public void addListenerLoadListener(ListenerLoadListener listener)
//    {
//        if (listener == null) {
//            return;
//        }
//        if (listenerLoadListener == null) {
//            listenerLoadListener = listener;
//        }
//        else if (listenerLoadListener instanceof ListenerLoadListeners) {
//            ((ListenerLoadListeners) listenerLoadListener).addListener(listener);
//        }
//        else {
//            ListenerLoadListeners listeners = new ListenerLoadListeners(listenerLoadListener);
//            listeners.addListener(listener);
//            listenerLoadListener = listeners;
//        }
//
//        if (log.isInfoEnabled()) {
//            log.info("Add listener load listener:" + listener);
//        }
//    }

//    /**
//     * 删除命令装载桢听者
//     *
//     * @param listener 命令装载桢听者
//     */
//    public void removeListenerLoadListener(ListenerLoadListener listener)
//    {
//        if (listener == null) {
//            return;
//        }
//        if (listenerLoadListener == listener) {
//            listenerLoadListener = null;
//        }
//        else if (listenerLoadListener instanceof ListenerLoadListeners) {
//            ((ListenerLoadListeners) listenerLoadListener).removeListener(listener);
//        }
//        if (log.isInfoEnabled()) {
//            log.info("Remove listener load listener:" + listener);
//        }
//    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("EventService");
        sb.append("{name=").append(getName()).append(",");
        sb.append("listeners=").append(listeners);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Namespace
     *
     * @param namespace Namespace
     */
    public void setNamespace(Namespace namespace)
    {
        this.namespace = namespace;
    }

    /**
     * 设置日志
     *
     * @param log Logger
     */
    public void setLogger(Logger log)
    {
        this.log = log;
    }

    /**
     * 返回日志
     *
     * @return Logger
     */
    public Logger getLogger()
    {
        return log;
    }

    /**
     * 处理事件
     *
     * @param event Event
     */
    public void doHandle(Object event) throws Exception
    {
        if (event instanceof Event) {
            doHandle((Event) event);
        }
        else {
            doHandle((GenericEvent) event);
        }
    }

    /**
     * 处理事件对象
     *
     * @param event 事件对象
     */
    private void doHandle(GenericEvent event) throws Exception
    {
        Name name = event.getRequestName();

        String modelName = name.get(2);
        GenericEventHandler handler = (GenericEventHandler) getHandler(modelName);
        if (handler == null) {
            log.error("No such handler:" + getName() + '/' + modelName);
            throw new NoSuchComponentException(this.getName(), modelName);
        }
        try {
            handler.handle(event);
        }
        catch (Throwable t) {
            log.error("Handling event exception", t);
        }
    }

    /**
     * 处理事件
     *
     * @param event Event
     */
    private void doHandle(Event event) throws Exception
    {
        Name name = event.getRequestName();

        String modelName = name.get(2);
        EventHandler handler = (EventHandler) getHandler(modelName);
        if (handler == null) {
            log.error("No such handler:" + getName() + '/' + modelName);
            throw new NoSuchComponentException(this.getName(), modelName);
        }
        try {
            handler.handle(event);
        }
        catch (Throwable t) {
            log.error("Handling event exception", t);
        }
    }
}
