package org.femtoframework.service.apsis;


import org.femtoframework.parameters.ParametersMap;
import org.femtoframework.service.Response;
import org.femtoframework.service.SessionID;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

/**
 * SimpleResponse
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-11-14 18:21:47
 */
public class ApsisResponse extends ParametersMap implements Response, Externalizable
{
    protected int statusCode;
    protected String statusMessage;
    protected Throwable throwable;

    public ApsisResponse()
    {
        super(new HashMap(4));
    }

    public ApsisResponse(int code)
    {
        this(code, (String)null);
    }

    public ApsisResponse(int code, String message)
    {
        this(code, message, null);
    }

    public ApsisResponse(int code, String message, Throwable throwable)
    {
        this();
        this.statusCode = code;
        this.statusMessage = message;
        this.throwable = throwable;
    }

    public ApsisResponse(int statusCode, Throwable throwable)
    {
        this(statusCode, null, throwable);
    }

    /**
     * 设置状态码
     *
     * @param statusCode 状态码
     */
    public void setCode(int statusCode)
    {
        this.statusCode = statusCode;
    }

    /**
     * 设置状态信息
     *
     * @param message 信息
     */
    public void setMessage(String message)
    {
        this.statusMessage = message;
    }

    /**
     * 设置异常
     *
     * @param throwable 异常
     * @since 2.4
     */
    public void setThrowable(Throwable throwable)
    {
        this.throwable = throwable;
    }

    /**
     * 返回Session标识
     *
     * @return [String] Session标识
     */
    public SessionID getSessionID()
    {
        return null;
    }

    /**
     * 设置会话标识(用于找到服务器会话和服务会话）
     *
     * @param sessionId 会话标识，如果是null，表示清除会话标识
     */
    public void setSessionID(SessionID sessionId)
    {
    }

    /**
     * 返回异常
     *
     * @return 异常
     */
    public Throwable getThrowable()
    {
        return throwable;
    }

    /**
     * 是否正确执行<br>
     * 等价于 #getCode() == SC_OK
     *
     * @return [boolean] 状态是否正确
     */
    public boolean isStatusOK()
    {
        return SC_OK == statusCode;
    }

    /**
     * 返回当前状态码
     *
     * @return [int] 状态码
     */
    public int getCode()
    {
        return statusCode;
    }

    /**
     * 返回状态信息
     *
     * @return [String] 状态信息
     */
    public String getMessage()
    {
        return this.statusMessage;
    }

    @Override
    public void writeExternal(ObjectOutput oos) throws IOException {
        super.writeExternal(oos);
        oos.writeInt(statusCode);
        oos.writeUTF(statusMessage);
        oos.writeObject(throwable);
    }

    @Override
    public void readExternal(ObjectInput ois) throws IOException, ClassNotFoundException {
        super.readExternal(ois);
        statusCode = ois.readInt();
        statusMessage = ois.readUTF();
        throwable = (Throwable) ois.readObject();
    }
}
