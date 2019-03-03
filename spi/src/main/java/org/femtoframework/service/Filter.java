package org.femtoframework.service;

/**
 * 请求和响应过滤器
 *
 * @author fengyun
 * @version 1.00 2005-5-4 23:38:44
 */
public interface Filter
{
    /**
     * 执行命令
     *
     * @param request  请求
     * @param response 响应
     * @param chain    过滤器链
     * @throws Exception 如果没法被过滤器接受时抛出
     */
    void filter(Request request, Response response, FilterChain chain) throws Exception;
}
