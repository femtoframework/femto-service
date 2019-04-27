package org.femtoframework.service.apsis.event;

import org.femtoframework.coin.Component;
import org.femtoframework.coin.Namespace;
import org.femtoframework.coin.NamespaceFactory;
import org.femtoframework.coin.spi.NamespaceFactoryAware;
import org.femtoframework.net.message.Message;
import org.femtoframework.net.message.MessageListener;
import org.femtoframework.net.message.MessageMetadata;
import org.femtoframework.net.message.RequestResponse;
import org.femtoframework.parameters.Parameters;
import org.femtoframework.service.*;
import org.femtoframework.service.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Name;

/**
 * 事件服务器
 *
 * @author fengyun
 * @version 1.00 2005-9-19 22:45:07
 */
public final class EventServer extends EventContainerBase
    implements Server, MessageListener, NamespaceFactoryAware
{
    private static final String SPERATOR = "###################################################";

    /**
     * 日志
     */
    private Logger log = LoggerFactory.getLogger("apsis/event_server");

    /**
     * 服务容器
     */
    private NamespaceFactory namespaceFactory;

    public void doHandle(Object event) throws Exception
    {
        Name name;
        if (event instanceof Event) {
            name = ((Event)event).getRequestName();
        }
        else if (event instanceof GenericEvent) {
            name = ((GenericEvent)event).getRequestName();
        }
        else {
            throw new IllegalArgumentException("Illegal event:" + event);
        }

        // server/service/command
        if (name.size() < 3) {
            log.error("Invalid request identify:" + name);
            throw new InvalidComponentNameException(name);
        }

        if (log.isDebugEnabled()) {
            log.debug(SPERATOR);
            log.debug(event.toString());
            log.debug(SPERATOR);
        }

        String namespace = name.get(1);
        Namespace ns = namespaceFactory.getNamespace(namespace, false);
        if (ns == null) {
            log.error("No such namespace:" + namespace);
            throw new NoSuchNamespaceException(name.toString());
        }

        if (event instanceof GenericEvent) {
            doHandle(ns, (GenericEvent)event);
        }
        else {
            doHandle(ns, (Event)event);
        }
    }

    /**
     * 当消息到达的时候调用
     *
     * @param metadata 消息元数据
     * @param message  消息
     */
    public void onMessage(MessageMetadata metadata, Object message)
    {
        if (message instanceof RequestResponse) {
            RequestResponse task = (RequestResponse)message;
            Message req = (Message)task.getRequest();
            EventCallbackHandler callbackHandler = null;
            EventCallback callback = null;
            //准备一个回掉
            if (req instanceof EventCallbackable) { //需要callback
                callbackHandler = ((EventCallbackable)req).getCallbackHandler();
                callback = new SimpleEventCallback(req);
            }
            try {
                handle(req);
            }
            catch (Throwable t) {
                if (log.isErrorEnabled()) {
                    log.error("Error while handling event for:" + req, t);
                }
                if (callback != null) {
                    callback.setException(t);
                }
            }
            finally {
                if (callbackHandler != null) { //需要callback
                    callbackHandler.callback(callback);
                }
            }
        }
        else {
            log.error("Invalid message:" + message);
        }
    }

    /**
     * 处理事件对象
     *
     * @param ns Namespace
     * @param event 事件对象
     */
    private void doHandle(Namespace ns, GenericEvent event) throws Exception
    {
        Name name = event.getRequestName();

        String compName = name.get(2);
        GenericEventHandler handler = (GenericEventHandler)getHandler(ns, compName, GenericEventHandler.class);
        if (handler == null) {
            log.error("No such handler:" + getName() + '/' + compName);
            throw new NoSuchComponentException(this.getName(), compName);
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
     * @param ns Namespace
     * @param event Event
     */
    private void doHandle(Namespace ns, Event event) throws Exception
    {
        Name name = event.getRequestName();

        String compName = name.get(2);
        EventHandler handler = (EventHandler) getHandler(ns, compName, EventHandler.class);
        if (handler == null) {
            log.error("No such handler:" + getName() + '/' + compName);
            throw new NoSuchComponentException(this.getName(), compName);
        }
        try {
            handler.handle(event);
        }
        catch (Throwable t) {
            log.error("Handling event exception", t);
        }
    }

    /**
     * 根据名称返回Model组件
     *
     * @param name 组件名称
     * @return Model组件
     */
    public Object getHandler(Namespace ns, String name, Class<?> expectedType)
    {
        Component component = ns.getComponentFactory().get(name);
        if (component == null) {
            return null;
        }
        Object bean = expectedType;
        if (expectedType.isAssignableFrom(bean.getClass())) {
            return bean;
        }
        else {
            Parameters<Object> attributes = component.getStatus().getAttributes();
            synchronized (component) {
                EventHandler eventHandler = (EventHandler)attributes.get(expectedType.getName());
                if (eventHandler == null) {
                    eventHandler = wrap(name, bean);
                    attributes.put(expectedType.getName(), eventHandler);
                }
                return eventHandler;
            }
        }
    }


    protected EventHandler wrap(String name, Object command)
    {
        if (!ListenerWrapper.isListener(command)) {
            log.warn("The model is not a valid event:" + command + " name:" + name);
            return null;
        }

        return ListenerWrapper.createWrapper(name, command);
    }

    @Override
    public final String toString()
    {
        return "EventServer";
    }

    /**
     * Set NamespaceFactory
     *
     * @param namespaceFactory NamespaceFactory
     */
    @Override
    public void setNamespaceFactory(NamespaceFactory namespaceFactory) {
        this.namespaceFactory = namespaceFactory;
    }
}
