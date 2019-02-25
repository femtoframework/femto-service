package org.femtoframework.service;

/**
 * No such component exception
 *
 * @author fengyun
 * @version 1.00 2005-9-6 22:03:46
 */
public class NoSuchComponentException extends FrameworkException
{
    /**
     * 构造
     *
     * @param namespace Namespace
     * @param component Component
     */
    public NoSuchComponentException(String namespace, String component)
    {
        super(Response.SC_COMPONENT_NOT_FOUND, "no_such_component", namespace, component);
    }
}
