package org.femtoframework.service;

import org.femtoframework.lang.Binary;
import org.femtoframework.net.HostPort;
import org.femtoframework.util.DataUtil;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * 服务器标识
 *
 * @author fengyun
 * @version 1.00 Mar 12, 2002 3:37:55 PM
 */
public class ServerID implements Externalizable, Cloneable {
    private transient String host;
    private transient int port;
    private transient int identity;

    //用HOST + PORT + Identity组成的统一标识
    private long id;

    /**
     * 用长整数来构造服务器标识
     *
     * @param id
     */
    public ServerID(long id) {
        setId(id);
    }

    /**
     * @param host
     * @param port
     */
    public ServerID(String host, int port) {
        setId(host, port, 0);
    }

    /**
     * @param host
     * @param port
     */
    public ServerID(String host, int port, int identity) {
        setId(host, port, identity);
    }

    /**
     * 根据id来初始化
     *
     * @param id
     */
    private void setId(long id) {
        this.id = id;
        int h = (int)(id & 0xFFFFFFFFL);
        int p = (int)((id >> 32) & 0xFFFFL);
        int i = (int)((id >> 48) & 0x7FFFL);
        StringBuilder sb = new StringBuilder();
        sb.append(((h >> 24) & 0xFF)).append('.');
        sb.append(((h >> 16) & 0xFF)).append('.');
        sb.append(((h >> 8) & 0xFF)).append('.');
        sb.append(((h) & 0xFF));

        this.host = sb.toString();
        this.port = p;
        this.identity = i;
    }

    /**
     * 根据id来初始化
     *
     * @param host     主机地址
     * @param port     端口
     * @param identity 标识
     */
    private void setId(String host, int port, int identity) {
        this.host = host;
        this.port = port;
        this.identity = identity;
        this.id = toId(host, port, identity);
    }

    /**
     * 返回主机IP地址
     *
     * @return 主机IP地址
     */
    public String getHost() {
        return host;
    }

    /**
     * 返回服务器侦听端口号
     *
     * @return 端口号
     */
    public int getPort() {
        return port;
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
     * 返回唯一标识
     *
     * @return 唯一标识
     */
    public long getId() {
        return id;
    }

    /**
     * 将主机端口对转换成长整标识
     *
     * @param host 主机
     * @param port 端口
     * @return 长整标识
     */
    public static long toId(String host, int port) {
        return toId(host, port, 0);
    }

    /**
     * 将主机端口对转换成长整标识
     *
     * @param host     主机
     * @param port     端口
     * @param identity 子标识号
     * @return 长整标识
     */
    public static long toId(String host, int port, int identity) {
        int[] array = DataUtil.toInts(host, '.');
        int h = 0;
        if (array != null && array.length == 4) {
            byte[] bytes = new byte[4];
            bytes[0] = (byte)array[0];
            bytes[1] = (byte)array[1];
            bytes[2] = (byte)array[2];
            bytes[3] = (byte)array[3];
            h = Binary.toInt(bytes);
        }
        else {
            throw new IllegalArgumentException("Invalid host:" + host);
        }
        return ((((long)identity) & 0x7FFFL) << 48) | ((((long)port) & 0xFFFFL) << 32) |
               (((long)h) & 0xFFFFFFFFL);
    }


    /**
     * 返回哈希码
     *
     * @return 哈希码
     */
    public int hashCode() {
        return host.hashCode() + port + identity;
    }

    /**
     * 返回对象是否与标识等效
     *
     * @return 如果等效返回<code>true</code> 反之则返回<code>false</code>
     */
    public boolean equals(Object obj) {
        if (obj instanceof ServerID) {
            ServerID sid = (ServerID)obj;
            return sid.id == id;
        }
        return false;
    }

    /**
     * 返回字符串标识
     *
     * @return 字符串标识
     */
    public String toString() {
        if (identity > 0) {
            return host + ':' + port + ':' + identity;
        }
        else {
            return host + ':' + port;
        }
    }

    /**
     * 克隆
     *
     * @see Cloneable
     */
    public Object clone() {
        ServerID sid = null;
        try {
            sid = (ServerID)super.clone();
        }
        catch (CloneNotSupportedException cnse) {
        }
        return sid;
    }


    private static ServerID local = null;

    /**
     * 返回本地服务器
     *
     * @return 本地服务器
     */
    public static ServerID getLocal() {
        if (local == null) {
            local = new ServerID(HostPort.getLocalHost(), HostPort.getLocalPort());
        }
        return local;
    }

    @Override
    public void writeExternal(ObjectOutput oos) throws IOException {
        oos.writeLong(id);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readLong();
    }
}
