package org.femtoframework.service.rmi;

import org.femtoframework.io.CodecUtil;
import org.femtoframework.util.StringUtil;

import java.io.*;

/**
 * 采用字符串作为唯一标识
 *
 * @author fengyun
 * @version 1.00 2005-8-14 1:18:58
 */
public class StrOID implements ObjID {
    /**
     * 对象唯一路径
     */
    private String uri;

    public StrOID() {
    }

    /**
     * 构造对象标示
     *
     * @param uri 对象URI
     */
    public StrOID(String uri) {
        this.uri = uri;
    }

    /**
     * Returns the hash code for the <code>ObjID</code> (the object number).
     */
    public int hashCode() {
        return getUri().hashCode();
    }

    /**
     * Two object identifiers are considered equal if they have the
     * same contents.
     *
     * @since JDK1.1
     */
    public boolean equals(Object obj) {
        if (obj instanceof StrOID) {
            StrOID objId = (StrOID)obj;
            return StringUtil.equals(getUri(), objId.getUri());
        }
        else {
            return false;
        }
    }

    public String toString() {
        return "StrOID[" + getUri() + "]";
    }


    @Override
    public void writeTo(OutputStream oos) throws IOException {
        CodecUtil.writeSingle(oos, uri);
    }

    /**
     * 反串行化
     *
     * @param ois 输入
     * @throws IOException    当发生I/O异常时
     * @throws ClassNotFoundException
     */
    public void readFrom(InputStream ois) throws IOException, ClassNotFoundException {
        uri = CodecUtil.readSingle(ois);
    }

    public String getUri() {
        return uri;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(getUri());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        uri = in.readUTF();
    }
}
