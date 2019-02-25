package org.femtoframework.service;

import java.util.EventObject;

/**
 * ComponentEvent
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2006-3-8 15:13:34
 */
public class ComponentEvent extends EventObject
{
    private String state;
    private String namespace;
    private String component;

    /**
     * Constructs a prototypical Event.
     *
     * @param    source    The object on which the Event initially occurred.
     * @param state 状态
     */
    public ComponentEvent(Object source, String namespace, String component, String state)
    {
        super(source);
        this.namespace = namespace;
        this.component = component;
        this.state = state;
    }


    public String getState()
    {
        return state;
    }


    public String getNamespace()
    {
        return namespace;
    }

    public String getComponent()
    {
        return component;
    }


}
