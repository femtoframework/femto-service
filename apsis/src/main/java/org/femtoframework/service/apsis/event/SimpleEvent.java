package org.femtoframework.service.apsis.event;

import org.femtoframework.net.message.RequestMessage;
import org.femtoframework.service.Event;
import org.femtoframework.service.Session;
import org.femtoframework.service.SessionTimeoutException;
import org.femtoframework.service.apsis.ext.AbstractArguments;
import org.femtoframework.service.event.EventCallbackHandler;

import javax.naming.Name;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * 简单的事件实现
 *
 * @author fengyun
 * @version 1.00 2005-9-19 22:06:16
 */
public class SimpleEvent extends AbstractArguments
    implements Event, RequestMessage
{
    private Name callbackHandlerName = null;
    private EventCallbackHandler callbackHandler;

    /**
     * 构造
     *
     * @param requestName 请求名称
     */
    public SimpleEvent(String requestName)
    {
        super(requestName);
        setTimeout(100);
    }

    /**
     * 返回当前用户Session
     *
     * @param create 如果<code>true</code>，当<code>session==null</code>时建会话
     * @param check  是否检查Session是否超时
     * @return [Session] 当前用户会话
     */
    public Session getSession(boolean create, boolean check) throws SessionTimeoutException
    {
        if (check && getSession() == null) {
            throw new SessionTimeoutException();
        }
        return getSession(create);
    }

    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("SimpleEvent");
        buf.append("{").append(super.toString()).append("}");
        return buf.toString();
    }

    /**
     * 输出对象
     *
     * @param oos 对象输出流
     */
    @Override
    public void writeExternal(ObjectOutput oos) throws IOException
    {
        super.writeExternal(oos);
        //如果对象需要序列化，那么将listener导出为远程调用
        oos.writeObject( callbackHandlerName);
        oos.writeObject( callbackHandler );
    }

    /**
     * 对象输入流
     *
     * @param ois 对象输入流
     */
    @Override
    public void readExternal(ObjectInput ois) throws IOException, ClassNotFoundException
    {
        super.readExternal(ois);
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

    public Name getCallbackHandlerName()
    {
        return callbackHandlerName;
    }

    public void setCallbackHandlerName(Name callbackHandler)
    {
        this.callbackHandlerName = callbackHandler;
    }
}
