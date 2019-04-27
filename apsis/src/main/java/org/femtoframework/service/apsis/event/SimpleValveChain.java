package org.femtoframework.service.apsis.event;

import org.femtoframework.service.Event;
import org.femtoframework.service.EventChain;
import org.femtoframework.service.EventHandler;
import org.femtoframework.service.EventValve;
import org.femtoframework.service.event.GenericEvent;
import org.femtoframework.service.event.GenericEventHandler;

import java.util.List;

/**
 * 简单ValveChain实现
 *
 * @author fengyun
 * @version 1.00 2005-9-19 22:18:16
 */
public class SimpleValveChain implements EventChain
{
    private List<EventValve> valves = null;

    private int index = 0;

    private int size;

    /**
     * 事件处理器
     */
    private EventHandler handler;

    private GenericEventHandler genericHandler;

    /**
     * 构造
     *
     * @param valves 过滤器
     */
    SimpleValveChain(EventHandler handler, List<EventValve> valves)
    {
        this.handler = handler;
        this.valves = valves;
        this.size = valves.size();
    }

    SimpleValveChain(GenericEventHandler genericHandler, List<EventValve> valves)
    {
        this.genericHandler = genericHandler;
        this.valves = valves;
        this.size = valves.size();
    }

    SimpleValveChain(List<EventValve> valves)
    {
        this.valves = valves;
    }

    /**
     * 传递给下一个阀门进行处理
     *
     * @param event Event
     */
    public void handleNext(Object event) throws Exception
    {
        if (index < size) {
            EventValve valve = valves.get(index++);
            valve.handle(event, this);
        }
        else if (index == size && handler != null) {
            if (event instanceof Event) {
                handler.handle((Event) event);
            }
            else {
                genericHandler.handle((GenericEvent) event);
            }
        }
    }
}
