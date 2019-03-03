package org.femtoframework.service;

/**
 * 执行异常
 *
 * @author fengyun
 * @version 1.00 2005-5-4 23:42:52
 */
public class ExecutionException extends FrameworkException
{
    /**
     * 构造
     *
     * @param code    状态码
     * @param message 消息
     * @param arg1    参数1
     */
    public ExecutionException(int code, String message, String arg1)
    {
        super(code, message, arg1);
    }

    /**
     * 构造
     *
     * @param code    状态码
     * @param message 消息
     * @param arg1    参数1
     * @param arg2    参数2
     */
    public ExecutionException(int code, String message, String arg1, String arg2)
    {
        super(code, message, arg1, arg2);
    }

    /**
     * 构造
     *
     * @param code    状态码
     * @param message 消息
     * @param arg1    参数1
     * @param arg2    参数2
     * @param arg3    参数3
     */
    public ExecutionException(int code, String message, String arg1, String arg2, String arg3)
    {
        super(code, message, arg1, arg2, arg3);
    }

    /**
     * 构造
     *
     * @param code    状态码
     * @param message 消息
     * @param arg1    参数1
     * @param arg2    参数2
     * @param arg3    参数3
     */
    public ExecutionException(int code, String message, String arg1, String arg2,
                              String arg3, Throwable cause)
    {
        super(code, message, arg1, arg2, arg3, cause);
    }

    /**
     * 构造
     *
     * @param code    状态码
     * @param message 消息
     * @param arg1    参数1
     * @param arg2    参数2
     */
    public ExecutionException(int code, String message, String arg1, String arg2, Throwable cause)
    {
        super(code, message, arg1, arg2, cause);
    }

    /**
     * 构造
     *
     * @param code    状态码
     * @param message 消息
     * @param arg1    参数1
     */
    public ExecutionException(int code, String message, String arg1, Throwable cause)
    {
        super(code, message, arg1, cause);
    }

    /**
     * 构造
     *
     * @param code    状态码
     * @param message 消息
     * @param args    参数
     */
    public ExecutionException(int code, String message, Object[] args)
    {
        super(code, message, args);
    }

    /**
     * 构造
     *
     * @param code    状态码
     * @param message 消息
     * @param args    参数
     */
    public ExecutionException(int code, String message, Object[] args, Throwable cause)
    {
        super(code, message, args, cause);
    }

    /**
     * 构造
     *
     * @param status [int] 状态码 <code>message == ""</code>
     */
    public ExecutionException(int status)
    {
        super(status);
    }

    /**
     * 构造
     *
     * @param status  [int] 状态码
     * @param message [String] 状态信息
     */
    public ExecutionException(int status, String message)
    {
        super(status, message);
    }

    /**
     * 构造
     *
     * @param status  [int] 状态码
     * @param message [String] 状态信息
     * @param cause   [Throwable] 成因
     */
    public ExecutionException(int status, String message, Throwable cause)
    {
        super(status, message, cause);
    }
}
