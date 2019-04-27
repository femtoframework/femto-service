package org.femtoframework.service.apsis.event;

import org.femtoframework.bean.AbstractLifecycle;
import org.femtoframework.bean.NamedBean;
import org.femtoframework.service.Event;
import org.femtoframework.service.EventChain;
import org.femtoframework.service.EventHandler;
import org.femtoframework.service.EventValve;
import org.femtoframework.service.event.GenericEvent;
import org.femtoframework.service.event.GenericEventHandler;

import java.util.List;

/**
 * 基础容器
 *
 * @author fengyun
 * @version 1.00 2005-9-19 22:41:04
 */
public abstract class EventContainerBase
    extends AbstractLifecycle
    implements EventValve, EventHandler, GenericEventHandler, NamedBean
{
    private String name;

    /**
     * Event Pipeline
     */
    protected SimplePipeline pipeline = new SimplePipeline();

    /**
     * 添加一个阀门
     *
     * @param valve 阀门
     */
    public void addValve(EventValve valve)
    {
        pipeline.addValve(valve);
    }

    /**
     * 返回阀门数组
     *
     * @return 阀门数组
     */
    public List<EventValve> getValves()
    {
        return pipeline.getValves();
    }

    /**
     * 删除给定的阀门
     *
     * @param valve 删除阀门
     */
    public void removeValve(EventValve valve)
    {
        pipeline.removeValve(valve);
    }

    /**
     * 初始化实现
     */
    public void _doInit()
    {
        pipeline.setName(getName() + "_pipeline");
        pipeline.setBasic(this);
    }

    /**
     * 处理事件
     *
     * @param event Event
     * @param chain 过滤器链
     */
    public void handle(Object event, EventChain chain) throws Exception
    {
        doHandle(event);

        chain.handleNext(event);
    }

    /**
     * 处理事件
     *
     * @param event Event
     */
    public abstract void doHandle(Object event) throws Exception;

    /**
     * 处理事件
     *
     * @param event Event
     */
    public void handle(Object event) throws Exception
    {
        pipeline.handle(event);
    }

    /**
     * 处理事件
     *
     * @param event Event
     */
    public void handle(Event event) throws Exception
    {
        pipeline.handle(event);
    }

    /**
     * 处理事件对象
     *
     * @param event 事件对象
     */
    public void handle(GenericEvent event) throws Exception
    {
        pipeline.handle(event);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
