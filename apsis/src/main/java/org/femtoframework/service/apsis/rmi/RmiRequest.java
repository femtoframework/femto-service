package org.femtoframework.service.apsis.rmi;

import org.femtoframework.io.CodecUtil;
import org.femtoframework.io.Streamable;
import org.femtoframework.net.message.RequestMessage;
import org.femtoframework.service.SessionID;
import org.femtoframework.service.StatefulElement;
import org.femtoframework.service.apsis.SessionLocal;
import org.femtoframework.service.apsis.SimpleSessionID;
import org.femtoframework.service.apsis.marshal.ApsisMarshaller;
import org.femtoframework.service.rmi.ObjID;
import org.femtoframework.service.rmi.StrOID;
import org.femtoframework.util.StringUtil;

import java.io.*;

/**
 * RMI请求
 *
 * @author fengyun
 * @version Feb 22, 2003 10:55:02 PM
 */
public class RmiRequest implements RequestMessage, Streamable, Externalizable, StatefulElement {
    /**
     * 会话标识（可选参数）
     */
    private SessionID sessionId;

    /**
     * 对象标识
     */
    private ObjID objId;

    /**
     * 方法标识
     */
    private long method;

    /**
     * 参数表
     */
    private Object[] arguments;

    /**
     * 请求超时时间
     */
    private int timeout;

    /**
     * 构造
     */
    public RmiRequest() {
        //获取当前线程的绑带会话标识
        this.sessionId = SessionLocal.getSessionID();
    }

    /**
     * 串行化
     *
     * @param oos 对象输出流
     * @throws IOException 当发生I/O异常时
     */
    public void writeTo(OutputStream oos)
        throws IOException {
        //StrOID
        CodecUtil.writeByte(oos, (byte)1);
        objId.writeTo(oos);
        CodecUtil.writeInt(oos,timeout);
        CodecUtil.writeLong(oos, method);
        CodecUtil.writeSingle(oos, sessionId != null ? sessionId.toString() : null);

        //每个线程一个，所以不能先取
        ApsisMarshaller marshaller = ApsisMarshaller.getInstance();
        marshaller.writeObject(oos, arguments);
    }

    /**
     * 反串行化
     *
     * @param ois 对象输入流
     * @throws IOException            当发生I/O异常时
     * @throws ClassNotFoundException
     */
    public void readFrom(InputStream ois)
        throws IOException, ClassNotFoundException {
        byte b = CodecUtil.readByte(ois);
        if (b == 1) {
            objId = new StrOID();
        }
        objId.readFrom(ois);
        timeout = CodecUtil.readInt(ois);
        method = CodecUtil.readLong(ois);
        String sid = CodecUtil.readSingle(ois);
        if (StringUtil.isValid(sid)) {
            setSessionID(sid);
        }

        //每个线程一个，所以不能先取
        ApsisMarshaller marshaller = ApsisMarshaller.getInstance();
        arguments = (Object[])marshaller.readObject(ois);
    }

    public ObjID getObjID() {
        return objId;
    }

    public void setObjID(ObjID id) {
        this.objId = id;
    }

    public long getMethod() {
        return method;
    }

    public void setMethod(long method) {
        this.method = method;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public SessionID getSessionID() {
        return sessionId;
    }

    public void setSessionID(String sessionId) {
        setSessionID(new SimpleSessionID(sessionId));
    }

    public void setSessionID(SessionID sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * 设置请求超时时间
     *
     * @param timeout 超时时间
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * 返回请求超时时间，如果请求超时时间是0，表示没有超时
     *
     * @return 请求超时时间
     */
    public int getTimeout() {
        return timeout;
    }

    @Override
    public void writeExternal(ObjectOutput oos) throws IOException {
        //StrOID
        oos.writeByte((byte)1);
        objId.writeExternal(oos);
        oos.writeInt(timeout);

        oos.writeLong(method);
        oos.writeUTF(sessionId != null ? sessionId.toString() : null);
        oos.writeObject(arguments);
    }

    @Override
    public void readExternal(ObjectInput ois) throws IOException, ClassNotFoundException {
        byte b = ois.readByte();
        if (b == 1) {
            objId = new StrOID();
        }
        else {
            throw new IllegalStateException("Invalid oid type:" + b);
        }
        objId.readExternal(ois);
        timeout = ois.readInt();
        method = ois.readLong();
        String sid = ois.readUTF();
        if (StringUtil.isValid(sid)) {
            setSessionID(sid);
        }
        arguments = (Object[])ois.readObject();
    }
}
