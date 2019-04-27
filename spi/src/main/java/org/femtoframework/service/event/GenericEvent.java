package org.femtoframework.service.event;

import org.femtoframework.io.DataCodec;
import org.femtoframework.net.message.RequestMessage;
import org.femtoframework.service.ActionElement;

import javax.naming.Name;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * 对普通事件对象的封装
 *
 * @author fengyun
 * @version 1.00 2005-9-20 11:23:21
 */
public class GenericEvent
    implements Externalizable, RequestMessage, ActionElement, EventCallbackable
{
    private Name callbackHandlerName = null;

    private EventCallbackHandler callbackHandler;

    /**
     * 事件对象
     */
    private Object[] eventArgs;

    /**
     * Action
     */
    private String action;

    /**
     * 请求名称
     */
    private Name requestName;


    public GenericEvent() {
    }

    /**
     * 构造
     *
     * @param requestName 请求名称
     */
    public GenericEvent(Name requestName)
    {
        if (requestName.size() != 3) {
            throw new IllegalArgumentException("Invalid request name:" + requestName);
        }
        this.requestName = requestName;
    }

    /**
     * 构造
     *
     * @param requestName 请求名称
     * @param event       事件
     */
    public GenericEvent(Name requestName, Object event)
    {
        this(requestName);
        setEventArgs(event);
    }

    /**
     * 构造
     *
     * @param requestName 请求名称
     * @param action      动作
     * @param eventArgs       事件
     */
    public GenericEvent(Name requestName, String action, Object... eventArgs)
    {
        this(requestName);
        setEventArgs(eventArgs);
        setAction(action);
    }

    private static final int PART_SERVER = 0;
    private static final int PART_NAMESPACE = 1;
    private static final int PART_COMPONENT = 2;

    /**
     * 返回要访问的服务器信息，可以是统配符或者是绝对地址
     *
     * @return 访问的的服务器信息
     */
    public String getServerName()
    {
        return requestName.get(PART_SERVER);
    }

    /**
     * 返回要访问的服务名称
     *
     * @return 需要访问的服务名称
     */
    public String getNamespace()
    {
        return requestName.get(PART_NAMESPACE);
    }

    /**
     * 返回访问的Model名称
     *
     * @return Model名称
     */
    public String getComponentName()
    {
        return requestName.get(PART_COMPONENT);
    }

    /**
     * 返回访问的Model名称
     * <p/>
     * Model定位符，用来唯一定位命令<br>
     * <p/>
     * <p/>
     * <pre>
     * 1. 说明:
     * <p/>
     * Server         服务器
     * Application    应用（是对服务器的会话）
     * Service        服务
     * Session        会话（是对服务的会话）
     * Model          组件
     * Command        命令
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
     * server     --> 服务器名
     * service    --> 服务名
     * model      --> 组件名
     * <p/>
     * 4. 例子：
     * 服务器:           "server"
     * 服务:             "server/service"
     * 组件:             "server/service/model"
     * <p/>
     * 所有服务器:               "*"
     * 任选一个服务器中的服务：  "+/service"
     * 访问当前服务器中的服务：  "$/service"
     * </pre>
     *
     * @return 请求名称
     */
    public Name getRequestName()
    {
        return requestName;
    }

    /**
     * 返回实际的事件对象
     *
     * @return 实际的事件对象
     */
    public Object getEvent()
    {
        if (eventArgs != null && eventArgs.length > 0) {
            return eventArgs[0];
        }
        return null;
    }


    /**
     * 返回实际的事件对象
     *
     * @return 实际的事件对象
     */
    public Object[] getEventArgs()
    {
        return eventArgs;
    }

    /**
     * 设置实际的事件对象
     *
     * @param eventArgs 实际的事件对象
     */
    public void setEventArgs(Object... eventArgs)
    {
        this.eventArgs = eventArgs;
    }

    /**
     * 返回请求超时时间，如果请求超时时间是0，表示没有超时
     *
     * @return 请求超时时间
     */
    public int getTimeout()
    {
        return 1000;
    }


    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("GenericEvent");
        sb.append("{requestName=").append(requestName);
        sb.append(",action=").append(action);
        sb.append(",event=");
        if (eventArgs != null) {
            sb.append("[");
            for(Object eventArg: eventArgs) {
                sb.append(eventArg).append(",");
            }
            sb.append("]");
        }
        else {
            sb.append("null");
        }
        sb.append('}');
        return sb.toString();
    }

    /**
     * 返回动作
     *
     * @return Action
     */
    public String getAction()
    {
        return action;
    }

    /**
     * 设置Action
     *
     * @param action Action
     */
    public void setAction(String action)
    {
        this.action = action;
    }

    public Name getCallbackHandlerName()
    {
        return callbackHandlerName;
    }

    public void setCallbackHandlerName(Name callbackHandler)
    {
        this.callbackHandlerName = callbackHandler;
    }


    @Override
    public void writeExternal(ObjectOutput oos) throws IOException {
        oos.writeObject( requestName);
        DataCodec.writeSingle(oos, action);
        oos.writeObject( eventArgs);
        oos.writeObject( callbackHandlerName);
        oos.writeObject( callbackHandler );
    }

    @Override
    public void readExternal(ObjectInput ois) throws IOException, ClassNotFoundException {
        requestName = (Name) ois.readObject();
        action = DataCodec.readSingle(ois);
        eventArgs = (Object[])ois.readObject();
        callbackHandlerName = (Name) ois.readObject();
        callbackHandler = (EventCallbackHandler) ois.readObject();
    }

    /**
     * Return the Event Callback Handler
     *
     * @return
     */
    public EventCallbackHandler getCallbackHandler() {
        return callbackHandler;
    }

    public void _setCallbackHandler(EventCallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }
}
