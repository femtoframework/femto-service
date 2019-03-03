package org.femtoframework.service;


import java.util.EventObject;


/**
 * General event for notifying listeners of significant changes on a Container.
 */

public final class ContainerEvent
    extends EventObject
{
    public static final int TYPE_START = 1;
    public static final int TYPE_REMOVE = 2;
    public static final int TYPE_STOP = 3;

    /**
     * The Container on which this event occurred.
     */
    private Container container = null;


    /**
     * The event data associated with this event.
     */
    private Object data = null;


    /**
     * The event type this instance represents.
     */
    private int type;


    /**
     * Construct a new ContainerEvent with the specified parameters.
     *
     * @param container Container on which this event occurred
     * @param type      Event type
     * @param data      Event data
     */
    public ContainerEvent(Container container, int type, Object data)
    {
        super(container);
        this.container = container;
        this.type = type;
        this.data = data;
    }


    /**
     * Return the event data of this event.
     */
    public Object getData()
    {
        return (this.data);
    }


    /**
     * Return the Container on which this event occurred.
     */
    public Container getContainer()
    {
        return (this.container);
    }


    /**
     * Return the event type of this event.
     */
    public int getType()
    {
        return (this.type);
    }


    /**
     * Return a string representation of this event.
     */
    public String toString()
    {
        return ("ContainerEvent['" + getContainer() + "','" +
                getType() + "','" + getData() + "']");
    }
}
