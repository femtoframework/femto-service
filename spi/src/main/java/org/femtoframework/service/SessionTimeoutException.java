package org.femtoframework.service;

/**
 * 会话超时异常
 *
 * @author fengyun
 * @version 1.00 2005-6-16 23:38:47
 */
public class SessionTimeoutException extends ExecutionException
{
    /**
     * 构造
     */
    public SessionTimeoutException()
    {
        super(Response.SC_SESSION_TIMEOUT);
    }
}
