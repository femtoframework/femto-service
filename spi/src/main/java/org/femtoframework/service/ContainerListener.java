package org.femtoframework.service;

import java.util.EventListener;


/**
 * Interface defining a listener for significant Container generated events.
 * Note that "container start" and "container stop" events are normally
 * LifecycleEvents, not ContainerEvents.
 */
public interface ContainerListener extends EventListener
{
    /**
     * Acknowledge the occurrence of the specified event.
     *
     * @param event ContainerEvent that has occurred
     */
    void handleContainerEvent(ContainerEvent event);
}
