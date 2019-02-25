package org.femtoframework.service;

/**
 * 服务器响应抽象定义
 *
 * @author fengyun
 * @version 1.1 2005-4-7 11:51:04
 */
public interface Response extends Return, StatefulElement
{
    /**
     * 错误请求
     */
    int SC_INVALID_COMPONENT_NAME = 303;

    /**
     * 请求超时
     */
    int SC_REQUEST_TIMEOUT = 304;

    /**
     * 系统忙
     */
    int SC_SYSTEM_BUSY = 305;

    /**
     * 无效的模型，类型不对
     */
    int SC_INVALID_MODEL = 306;

    /**
     * 会话超时
     */
    int SC_SESSION_TIMEOUT = 307;

    /**
     * 会话超时，需要前台响应认证
     */
    int SC_AUTH_REQUIRED = 308;


    /**
     * 异常包裹，用来兼容framework3
     */
    int SC_WRAPPED_EXCEPTION = 310;

    /**
     * 对象找不到
     */
    int SC_OBJECT_NOT_FOUND = 401;

    /**
     * 服务器找不到
     */
    int SC_SERVER_NOT_FOUND = 402;

    /**
     * 服务找不到
     */
    int SC_NAMESPACE_NOT_FOUND = 403;

    /**
     * 模块找不到
     */
    int SC_COMPONENT_NOT_FOUND = 404;

    /**
     * Action找不到
     */
    int SC_ACTION_NOT_FOUND = 409;

    /**
     * 请求校验异常
     */
    int SC_VALIDATION_ERROR = 408;

    /**
     * 运行期错误
     */
    int SC_RUNTIME_EXCEPTION = 410;

    /**
     * 无效的请求类型
     */
    int SC_INVALID_REQUEST_TYPE = 412;

    /**
     * 服务器当机
     */
    int SC_SERVER_DOWN = 503;

    /**
     * Framework出始化异常
     */
    int SC_FRAMEWORK_INITIAL_ERROR = 504;

    /**
     * 设置状态码
     *
     * @param statusCode 状态码
     */
    void setCode(int statusCode);

    /**
     * 设置状态信息
     *
     * @param message 信息
     */
    void setMessage(String message);

    /**
     * 设置异常
     *
     * @param throwable 异常
     * @since 2.4
     */
    void setThrowable(Throwable throwable);
}
