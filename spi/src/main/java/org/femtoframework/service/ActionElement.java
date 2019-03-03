package org.femtoframework.service;

/**
 * Action Element 用来标识那些需要根据字符串来定位到不同的功能模块上的请求或者事件
 *
 * @author fengyun
 * @version 1.00 2005-9-19 20:06:06
 */
public interface ActionElement
{
    /**
     * 返回动作
     *
     * @return Action
     */
    String getAction();
}
