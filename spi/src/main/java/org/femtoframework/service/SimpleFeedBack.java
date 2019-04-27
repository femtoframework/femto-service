package org.femtoframework.service;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * SimpleFeedBack
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-11-14 18:31:09
 */
public class SimpleFeedBack implements FeedBack, Externalizable
{
    private int code = SC_OK;
    private String message;

    public SimpleFeedBack()
    {
    }

    public SimpleFeedBack(int code)
    {
        this.code = code;
    }

    public SimpleFeedBack(int code, String message)
    {
        this.code = code;
        this.message = message;
    }

    /**
     * 是否正确执行<br>
     * 等价于 #getCode() == SC_OK
     *
     * @return [boolean] 状态是否正确
     */
    public boolean isStatusOK()
    {
        return SC_OK == code;
    }

    /**
     * 返回当前状态码
     *
     * @return [int] 状态码
     */
    public int getCode()
    {
        return code;
    }

    /**
     * 返回状态信息
     *
     * @return [String] 状态信息
     */
    public String getMessage()
    {
        return message;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * 串行化
     *
     * @param oos 对象输出流
     * @throws java.io.IOException 当发生I/O异常时
     */
    public void writeExternal(ObjectOutput oos) throws IOException
    {
        oos.writeInt(code);
        oos.writeUTF(message);
    }

    /**
     * 反串行化
     *
     * @param ois 对象输入流
     * @throws java.io.IOException    当发生I/O异常时
     * @throws ClassNotFoundException
     */
    public void readExternal(ObjectInput ois) throws IOException, ClassNotFoundException
    {
        code = ois.readInt();
        message = ois.readUTF();
    }

    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("FeedBack");
        buf.append("{code=").append(code);
        if (message != null) {
            buf.append(",message=").append(message);
        }
        buf.append('}');
        return buf.toString();
    }

    public boolean equals(Object o)
    {
        return this == o;
    }

    public int hashCode()
    {
        int result;
        result = code;
        result = 29 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}
