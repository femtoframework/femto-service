package org.femtoframework.service;

/**
 * 可以执行的
 *
 * @author fengyun
 * @version 1.00 2005-9-5 17:47:18
 */
public interface Executable
{
    /**
     * 参数类
     */
    Class[] PARAM_CLASSES = new Class[]{Request.class, Response.class};

    /**
     * 执行
     *
     * @param request  请求
     * @param response 响应
     * @throws Exception 异常
     */
    void execute(Request request, Response response)
        throws Exception;
}
