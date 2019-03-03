package org.femtoframework.service;

import org.femtoframework.util.i18n.LocalizedStatusException;

/**
 * 构架异常
 *
 * @author fengyun
 * @version 1.00 Mar 6, 2002 6:46:26 PM
 */
public class FrameworkException extends LocalizedStatusException
{

    /**
     * 构造
     *
     * @param status [int] 状态码 <code>message == ""</code>
     */
    public FrameworkException(int status) {
        super(status);
    }

    /**
     * 构造
     *
     * @param status [int] 状态码 <code>message == ""</code>
     * @param arg
     */
    public FrameworkException(int status, Object... arg) {
        super(status, arg);
    }

    /**
     * 构造
     *
     * @param code    状态码
     * @param message 消息
     * @param args    参数
     */
    public FrameworkException(int code, String message, Object... args) {
        super(code, message, args);
    }

    /**
     * 构造
     *
     * @param code    状态码
     * @param message 消息
     * @param cause
     * @param args    参数
     */
    public FrameworkException(int code, String message, Throwable cause, Object... args) {
        super(code, message, cause, args);
    }

    {
        setResourcesURI("org.femtoframework.service.resources.error");
    }

}
