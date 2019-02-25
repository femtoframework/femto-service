package org.femtoframework.service;


import org.femtoframework.parameters.Parameters;

import javax.naming.Name;
import java.util.Locale;

/**
 * 一次访问的参数集
 *
 * @author fengyun
 * @version 1.00 删除了无用方法 2005-4-7 11:44:20
 *          1.00 Apr 27, 2002 6:49:58 PM
 */
public interface Arguments
    extends Parameters, ActionElement, StatefulElement
{
    /**
     * 返回要访问的服务器信息，可以是统配符或者是绝对地址
     *
     * @return 访问的的服务器信息
     */
    String getServerName();

    /**
     * Namespace of the component
     *
     * @return Namespace of the component
     */
    String getNamespace();

    /**
     * 返回访问的Component名称
     *
     * @return Component 名称
     */
    String getComponentName();

    /**
     * 返回访问的Component名称
     * <p/>
     * Component定位符，用来唯一定位命令<br>
     * <p/>
     * <p/>
     * <pre>
     * 1. 说明:
     * <p/>
     * Server         服务器
     * Application    应用（是对服务器的会话）
     * Namespace      Namespace
     * Session        会话（是对服务的会话）
     * Component      组件
     * Command        命令(One type of component)
     * Request        请求
     * Response       响应
     * <p/>
     * 2. 定义:
     * <p/>
     * "*"        --> 所有的
     * "+"        --> 其中任一个
     * "$"        --> 当前服务器
     * "/"        --> 分隔符
     *  %         --> 所有远程服务器
     *  @         --> 优先选择本地的服务器
     *  #type     --> 指定类型的服务器
     *  =selectorType  --> 指定采用什么样的选取器（random round_robin hash)
     *  !host:port     --> 表示向指定的主机和端口的服务器发送
     * <p/>
     * 3. 变量：
     * server       --> 服务器名
     * namespace    --> 名字空间
     * component     --> 组件名
     * <p/>
     * 4. 例子：
     * 服务器:           "server"
     * 名字空间:         "server/namespace"
     * 组件:             "server/namespace/component"
     * <p/>
     * 所有服务器:               "*"
     * 任选一个服务器中的名字空间：  "+/namespace"
     * 访问当前服务器中的名字空间：  "$/namespace"
     * </pre>
     *
     * @return 请求名字
     */
    Name getRequestName();

    /**
     * 设置会话标识(用于找到服务器会话和服务会话）
     *
     * @param sessionId 会话标识，如果是null，表示清除会话标识
     */
    void setSessionID(String sessionId);

    //For I18N
    /**
     * 返回<code>java.util.Locale</code>
     *
     * @return Locale
     */
    Locale getLocale();

    /**
     * 设置区域信息，不会更新字符集
     *
     * @param locale <code>java.util.Locale</code>
     */
    void setLocale(Locale locale);

    /**
     * 设置区域信息，会自动设置charset
     *
     * @param locale 区域信息
     */
    void setLocale(String locale);

    /**
     * 设置请求action
     *
     * @param action Action
     */
    void setAction(String action);

    /**
     * 设置请求超时时间
     *
     * @param timeout 超时时间
     */
    void setTimeout(int timeout);

    /**
     * 返回请求超时时间，如果请求超时时间是0，表示没有超时
     *
     * @return 请求超时时间
     */
    int getTimeout();
}
