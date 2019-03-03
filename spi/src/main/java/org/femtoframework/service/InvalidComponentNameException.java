package org.femtoframework.service;

import javax.naming.Name;

/**
 * InvalidComponentNameException
 *
 * @author renex
 * @version 2006-9-29 14:14:56
 */
public class InvalidComponentNameException extends FrameworkException
{
    /**
     * 构造
     *
     * @param requestName Request Name
     */
    public InvalidComponentNameException(String requestName)
    {
        super(Response.SC_INVALID_COMPONENT_NAME, requestName);

    }

    /**
     * 构造
     *
     * @param requestName Request Name
     */
    public InvalidComponentNameException(Name requestName)
    {
        super(Response.SC_INVALID_COMPONENT_NAME, requestName.toString());

    }
}
