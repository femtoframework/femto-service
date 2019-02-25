package org.femtoframework.service;


/**
 * 过滤器链
 *
 * @author fengyun
 * @version 1.00 Jul 10, 2003 7:30:22 PM
 */
public interface FilterChain
{
    /**
     * 执行过滤
     *
     * @param request  请求
     * @param response 响应
     * @throws Exception 如果没法被过滤器接受时抛出
     */
    void filter(Request request, Response response) throws Exception;
}
