package org.femtoframework.service;

import org.femtoframework.implement.ImplementUtil;
import org.femtoframework.service.event.EventCallbackHandler;

import java.io.Serializable;

/**
 * EventContext
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-11-14 18:32:44
 */
public abstract class EventContext
{
    protected static EventContext context;

    private static EventContext getContext()
    {
        if (context == null) {
            context = ImplementUtil.getInstance(EventContext.class, true);
        }
        return context;
    }

    public static Event lookup(String name)
    {
        return getContext().lookupEvent(name);
    }

    public static FeedBack submit(Event event)
    {
        return getContext().submitIt(event);
    }

    public static FeedBack submit(String name, String action, Serializable event, String callbackHandlerName)
    {
        return getContext().submitIt(name, action, event, callbackHandlerName);
    }

    public static FeedBack submit(String name, String action, String callbackHandlerName, Serializable... eventArgs)
    {
        return getContext().submitIt(name, action, callbackHandlerName, eventArgs);
    }

    public static FeedBack submit(String name, String action, Serializable event)
    {
        return submit(name, action, event, null);
    }

    public static FeedBack submit(String name, Serializable event, String callbackHandlerName)
    {
        return submit(name, null, event, callbackHandlerName);
    }

    public static FeedBack submit(String name, Serializable event)
    {
        return submit(name, null, event, null);
    }

    public abstract FeedBack submitIt(String name, String action, String callbackHandlerName, Serializable...  eventArgs);

    public abstract FeedBack submitIt(String name, String action, Serializable event, String callbackHandlerName);

    public abstract FeedBack submitIt(Event event);

    public abstract Event lookupEvent(String name);

    /**
     * 发送请求<br>
     * 同步处理<br>
     *
     * @param events 请求
     * @return 返回响应
     */
    public static FeedBack[] submit(Event[] events)
    {
        //该方法下面的实际只是为了让某些子类拥有默认的实现
        FeedBack[] returns = new FeedBack[events.length];
        for (int i = 0; i < events.length; i++) {
            returns[i] = submit(events[i]);
        }
        return returns;
    }
}
