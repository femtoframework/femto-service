package org.femtoframework.service.apsis.rmi;

import org.femtoframework.io.CodecUtil;
import org.femtoframework.io.Streamable;
import org.femtoframework.net.message.ResponseMessage;
import org.femtoframework.service.SessionID;
import org.femtoframework.service.apsis.SimpleSessionID;
import org.femtoframework.service.apsis.marshal.ApsisMarshaller;
import org.femtoframework.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * RMI响应
 *
 * @author fengyun
 * @version Feb 22, 2003 11:01:49 PM
 */
public class RmiResponse
    implements ResponseMessage, Streamable
{
    /**
     * 是否执行异常
     */
    private boolean exception = false;

    /**
     * 会话标识（可选参数）
     */
    private SessionID sessionId;

    /**
     * 方法返回结果或者异常
     */
    private Object result;

    public SessionID getSessionID()
    {
        return sessionId;
    }

    public void setSessionID(SessionID sessionId)
    {
        this.sessionId = sessionId;
    }

    public void setSessionID(String sessionId)
    {
        setSessionID(new SimpleSessionID(sessionId));
    }

    public RmiResponse()
    {
    }

    public boolean isException()
    {
        return exception;
    }

    public void setException(boolean exception)
    {
        this.exception = exception;
    }

    public Object getResult()
    {
        return result;
    }

    public void setResult(Object result)
    {
        this.result = result;
    }

    /**
     * 串行化
     *
     * @param oos 对象输出流
     * @throws IOException 当发生I/O异常时
     */
    public void writeTo(OutputStream oos)
        throws IOException
    {
        CodecUtil.writeBoolean(oos, exception);
        CodecUtil.writeSingle(oos, sessionId != null ? sessionId.toString() : null);
        //每个线程一个，所以不能先取
        ApsisMarshaller marshaller = ApsisMarshaller.getInstance();
        marshaller.writeObject(oos, result);
    }

    /**
     * 反串行化
     *
     * @param ois 对象输入流
     * @throws IOException            当发生I/O异常时
     * @throws ClassNotFoundException
     */
    public void readFrom(InputStream ois)
        throws IOException, ClassNotFoundException
    {
        exception = CodecUtil.readBoolean(ois);
        String sid = CodecUtil.readSingle(ois);
        if (StringUtil.isValid(sid)) {
            setSessionID(sid);
        }
        ApsisMarshaller marshaller = ApsisMarshaller.getInstance();
        result = marshaller.readObject(ois);
    }
}
