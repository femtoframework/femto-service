package org.femtoframework.service;

import java.io.*;

/**
 * 服务标识是有服务器标识+一个[0-0xFFFF]的整数组成的
 *
 * @author fengyun
 * @version 1.00 2005-5-22 11:16:48
 */
public class NamespaceID implements Externalizable, Cloneable {
    private long serverId;
    private int identity;

    /**
     * @param serverId
     * @param identity
     */
    public NamespaceID(long serverId, int identity) {
        this.serverId = serverId;
        this.identity = identity;
    }

    /**
     * 内部区别于同类型的对象的唯一标识
     *
     * @return 唯一标识
     */
    public int getIdentity() {
        return identity;
    }

    /**
     * 返回服务器标识
     *
     * @return
     */
    public long getServerId() {
        return serverId;
    }

    /**
     * 返回哈希码
     *
     * @return 哈希码
     */
    public int hashCode() {
        return (int)(serverId & 0xFFFFFFF + serverId >> 32 + identity);
    }

    /**
     * 返回对象是否与标识等效
     *
     * @return 如果等效返回<code>true</code> 反之则返回<code>false</code>
     */
    public boolean equals(Object obj) {
        if (obj instanceof NamespaceID) {
            NamespaceID id = (NamespaceID)obj;
            return serverId == id.serverId && identity == id.identity;
        }
        return false;
    }

    /**
     * 字符串信息
     */
    private transient String str;

    /**
     * 返回字符串标识
     *
     * @return 字符串标识
     */
    public String toString() {
        if (str == null) {
            int h = (int)(serverId & 0xFFFFFFFFL);
            int p = (int)((serverId >> 32) & 0xFFFFL);
            int i = (int)((serverId >> 48) & 0x7FFFL);
            StringBuilder sb = new StringBuilder(32);
            sb.append(((h >> 24) & 0xFF)).append('.');
            sb.append(((h >> 16) & 0xFF)).append('.');
            sb.append(((h >> 8) & 0xFF)).append('.');
            sb.append(((h) & 0xFF));
            sb.append(':').append(p);
            sb.append(':').append(i);
            sb.append(':').append(identity);
            str = sb.toString();
        }
        return str;
    }

    /**
     * 克隆
     *
     * @see Cloneable
     */
    public Object clone() throws CloneNotSupportedException {
        NamespaceID sid = null;
        try {
            sid = (NamespaceID)super.clone();
        }
        catch (CloneNotSupportedException cnse) {
        }
        return sid;
    }

    @Override
    public void writeExternal(ObjectOutput oos) throws IOException {
        oos.writeLong(serverId);
        oos.write((identity >>> 8) & 0xFF);
        oos.write(identity & 0xFF);
    }

    @Override
    public void readExternal(ObjectInput ois) throws IOException, ClassNotFoundException {
        serverId = ois.readLong();

        int ch1 = ois.read();
        int ch2 = ois.read();
        if ((ch1 | ch2) < 0) {
            throw new EOFException();
        }
        identity = (ch1 << 8) + (ch2);
    }
}
