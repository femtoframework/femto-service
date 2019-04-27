package org.femtoframework.service.apsis.event;


import org.femtoframework.bean.Nameable;
import org.femtoframework.bean.NamedBean;
import org.femtoframework.service.EventChain;
import org.femtoframework.service.EventValve;
import org.femtoframework.service.event.EventPipeline;

import java.util.ArrayList;
import java.util.List;

/**
 * Event Pipeline
 *
 * @author fengyun
 * @version 1.00 2005-9-19 22:30:15
 */
public class SimplePipeline
    implements EventPipeline, NamedBean, Nameable
{
    private String name;

    /**
     * The basic Valve (if any) associated with this Pipeline.
     */
    private EventValve basic;

    /**
     * 过滤器
     */
    private List<EventValve> list = new ArrayList<>(2);

    /**
     * 返回基础过滤器
     *
     * @return 基础过滤器
     */
    public EventValve getBasic()
    {
        return basic;
    }

    /**
     * 设置基础过滤器
     *
     * @param valve 基础过滤器
     */
    public void setBasic(EventValve valve)
    {
        this.basic = valve;
    }

    /**
     * 添加一个过滤器
     *
     * @param valve 过滤器
     */
    public void addValve(EventValve valve)
    {
        list.add(valve);
    }

    /**
     * 添加事件桢听者
     *
     * @param listener 事件桢听者
     */
    public void addListener(String name, Object listener)
    {
        if (listener instanceof EventValve) {
            addValve((EventValve) listener);
        }
        else {
            addValve(ListenerWrapper.createWrapper(name, listener));
        }
    }

    /**
     * 返回过滤器数组
     *
     * @return 过滤器数组
     */
    public List<EventValve> getValves()
    {
        return list;
    }

    /**
     * 删除给定的过滤器
     *
     * @param valve 删除过滤器
     */
    public void removeValve(EventValve valve)
    {
        list.remove(valve);
    }

    /**
     * 处理事件
     *
     * @param event Event
     */
    public <E> void handle(E event) throws Exception
    {
        EventChain chain;
        if (!list.isEmpty()) {
            chain = new SimpleValveChain(getValves());
            chain.handleNext(event);
        }
        else {
            chain = EmptyValveChain.getInstance();
        }

        if (basic != null) {
            basic.handle(event, chain);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
