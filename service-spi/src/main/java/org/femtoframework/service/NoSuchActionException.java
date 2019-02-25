package org.femtoframework.service;

/**
 * No Such Action Exception
 *
 * @author fengyun
 * @version 1.00 2005-9-6 22:10:40
 */
public class NoSuchActionException extends FrameworkException
{
    /**
     * 构造
     *
     * @param component Component
     * @param action    Action
     */
    public NoSuchActionException(String component, String action)
    {
        super(Response.SC_ACTION_NOT_FOUND, "no_such_action", component, action);
    }
}
