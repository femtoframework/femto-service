package org.femtoframework.service.apsis.event;

import org.femtoframework.coin.CoinUtil;
import org.femtoframework.net.message.RequestMessage;
import org.femtoframework.parameters.Parameters;
import org.femtoframework.service.*;
import org.femtoframework.service.apsis.ApsisBalancer;
import org.femtoframework.service.apsis.ApsisClient;
import org.femtoframework.service.apsis.ApsisSessionID;
import org.femtoframework.service.apsis.local.LocalClient;
import org.femtoframework.service.apsis.naming.ApsisName;
import org.femtoframework.service.client.ClientUtil;
import org.femtoframework.service.event.EventCallbackHandler;
import org.femtoframework.service.event.EventCallbackable;
import org.femtoframework.service.event.GenericEvent;
import org.femtoframework.service.rmi.RmiUtil;
import org.femtoframework.util.status.StatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Name;
import java.io.Serializable;
import java.rmi.Remote;

/**
 * ApsisEventContext
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-11-14 18:34:05
 */
public class ApsisEventContext extends EventContext {
    private ApsisBalancer balancer = ClientUtil.getBalancer();

    protected ApsisClient getClient(Name name, ApsisSessionID sid,
                                    StatefulElement element, Parameters parameters)
        throws StatusException {
        return balancer.apsisBalance(name, sid, element, parameters);
    }

    @Override
    public FeedBack submitIt(String name, String action, String callbackHandlerName, Serializable... eventArgs) {
        GenericEvent ge = new GenericEvent(new ApsisName(name), action, eventArgs);
        if (callbackHandlerName != null) {
            ge.setCallbackHandlerName(new ApsisName(callbackHandlerName));
        }
        return submitIt(ge);
    }

    public FeedBack submitIt(String name, String action, Serializable event, String callbackHandlerName) {
       return submitIt(name, action, callbackHandlerName, event);
    }

    public FeedBack submitIt(GenericEvent event) {
        return submit0(event.getRequestName(), null, event);
    }

    public FeedBack submitIt(Event event) {
        return submit0(event.getRequestName(), (ApsisSessionID)event.getSessionID(), (SimpleEvent)event);
    }

    public Event lookupEvent(String name) {
        return new SimpleEvent(name);
    }

    private static Logger log = LoggerFactory.getLogger(ApsisEventContext.class);

    private FeedBack submit0(Name name, ApsisSessionID asid, RequestMessage event) {
        StatefulElement element = event instanceof StatefulElement ? (StatefulElement)event : null;
        Parameters parameters = event instanceof Parameters ? (Parameters)event : null;
        ApsisClient client;
        try {
            client = getClient(name, asid, element, parameters);
        }
        catch (StatusException e) {
            return new SimpleFeedBack(e.getCode(), e.getMessage());
        }
        if (client == null) {
            return new SimpleFeedBack(Response.SC_SERVER_NOT_FOUND, name.toString());
        }
        //如果不是本地调用，并且需要callback，那么就将监听器设置成远程模式
        if (event instanceof EventCallbackable) {
            Name callbackHandlerName = ((EventCallbackable)event).getCallbackHandlerName();
            if (callbackHandlerName != null) {
                Object bean = CoinUtil.getModule().getLookup().lookupBean(callbackHandlerName);
                if (bean instanceof EventCallbackHandler) {
                    if (!(client instanceof LocalClient)) {
                        Remote remote = RmiUtil.exportObject(bean, name.toString());
                        ((EventCallbackable)event)._setCallbackHandler((EventCallbackHandler)remote);
                    }
                    else {
                        ((EventCallbackable)event)._setCallbackHandler((EventCallbackHandler)bean);
                    }
                }
                else {
                    throw new IllegalStateException("The callback handler is not an implementation of EventCallbackHandler:" + bean);
                }
            }
        }
        try {
            client.send(event);
        }
        catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Sending event exception", e);
            }
            return new SimpleFeedBack(Response.SC_RUNTIME_EXCEPTION, e.getMessage());
        }
        return new SimpleFeedBack();
    }
}
