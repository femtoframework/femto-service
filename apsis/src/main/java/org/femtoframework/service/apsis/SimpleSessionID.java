package org.femtoframework.service.apsis;

import org.femtoframework.lang.Binary;
import org.femtoframework.service.ServerID;
import org.femtoframework.util.DataUtil;
import org.femtoframework.util.crypto.MD5;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * 会话标识
 *
 * @author fengyun
 * @version 1.00 2005-5-22 11:42:11
 */
public class SimpleSessionID implements ApsisSessionID, Externalizable, Cloneable
{
    /**
     * 服务器标识
     */
    private long serverId;

    /**
     * 会话标识
     */
    private int identity;

    /**
     * 校验数字
     */
    private int validation;

    /**
     * 字符串信息
     */
    private transient String str;

    private static final char SEP = ':';

    /**
     * 构造
     *
     * @param str 字符串信息
     */
    public SimpleSessionID(String str)
    {
        parse(str);
    }

    /**
     * 构造
     *
     * @param serverId   服务器标识
     * @param identity   标识
     * @param validation
     */
    public SimpleSessionID(long serverId, int identity, int validation)
    {
        this.serverId = serverId;
        this.identity = identity;
        this.validation = validation;
    }


    private void parse(String str)
    {
        if (str == null) {
            throw new IllegalArgumentException("Null session id");
        }
        int count = DataUtil.countChar(str, SEP);
        if (count != 4) {
            throw new IllegalArgumentException("Invalid session id:" + str);
        }

        int end = str.indexOf(SEP);
        String host = str.substring(0, end);
        int begin = end + 1;
        end = str.indexOf(SEP, begin);
        int port = Integer.parseInt(str.substring(begin, end));
        begin = end + 1;
        end = str.indexOf(SEP, begin);
        int sid = Integer.parseInt(str.substring(begin, end));
        serverId = ServerID.toId(host, port, sid);
//        begin = end + 1;
//        end = str.indexOf(SEP, begin);
//        serverID = Integer.parseInt(str.substring(begin, end));
        begin = end + 1;
        end = str.indexOf(SEP, begin);
        identity = Integer.parseInt(str.substring(begin, end));
        begin = end + 1;
        String s = str.substring(begin);
        if (s.length() != 8) {
            throw new IllegalArgumentException("Invalid session id:" + str);
        }

        validation = Integer.parseInt(s);
        this.str = str;
    }


    /**
     * 返回校验因子，根据服务器和会话唯一标识校验
     *
     * @return 返回校验引子
     */
    public byte[] getKey()
    {
        byte[] bytes = new byte[12];
        Binary.append(bytes, 0, serverId);
        Binary.append(bytes, 8, identity);
        return bytes;
    }

    /**
     * 判断会话标识是否有效
     *
     * @return 是否有效
     */
    public boolean isValid()
    {
        byte[] key = MD5.encrypt(getKey());

        int v1, v2;
        int v = validation;
        v1 = ((key[12] & 0x40) | (key[13] & 0x30) | (key[14] & 0x0C) | (key[15] & 0x03)) % 10;
        v2 = v % 10;
        if (v1 != v2) {
            return false;
        }

        v /= 100;

        v1 = ((key[8] & 0x40) | (key[9] & 0x30) | (key[10] & 0x0C) | (key[11] & 0x03)) % 10;
        v2 = v % 10;
        if (v1 != v2) {
            return false;
        }

        v /= 100;

        v1 = ((key[4] & 0x40) | (key[5] & 0x30) | (key[6] & 0x0C) | (key[7] & 0x03)) % 10;
        v2 = v % 10;
        if (v1 != v2) {
            return false;
        }

        v /= 100;

        v1 = ((key[0] & 0x40) | (key[1] & 0x30) | (key[2] & 0x0C) | (key[3] & 0x03)) % 10;
        v2 = v % 10;
        return v1 == v2;
    }

    /**
     * 内部区别于同类型的对象的唯一标识
     *
     * @return 唯一标识
     */
    public int getIdentity()
    {
        return identity;
    }

    /**
     * 返回扩展标识
     *
     * @return 扩展标识
     */
    public int getExtension()
    {
        return (int) ((serverId >> 48) & 0x7FFFL);
    }

    /**
     * 返回服务器标识
     *
     * @return 服务器标识
     */
    public long getServerId()
    {
        return serverId;
    }

    /**
     * 返回哈希码
     *
     * @return 哈希码
     */
    public int hashCode()
    {
        return (int) (serverId & 0xFFFFFFF + serverId >> 32 + identity + validation);
    }

    /**
     * 返回对象是否与标识等效
     *
     * @return 如果等效返回<code>true</code> 反之则返回<code>false</code>
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof SimpleSessionID) {
            SimpleSessionID id = (SimpleSessionID) obj;
            return serverId == id.serverId
                   && identity == id.identity
                   && validation == id.validation;
        }
        return false;
    }

    /**
     * 返回格式化后的字符串格式
     */
    public String normalize()
    {
        StringBuilder sb = new StringBuilder(48);
        sb.append(serverId);
        sb.append(':').append(identity);
        sb.append(':').append(validation);
        return sb.toString();
    }

    private static final char[] ZERO = new char[]{'0', '0', '0', '0', '0', '0', '0', '0'};

    /**
     * 返回字符串标识
     *
     * @return 字符串标识
     */
    public String toString()
    {
        if (str == null) {
            int h = (int) (serverId & 0xFFFFFFFFL);
            int p = (int) ((serverId >> 32) & 0xFFFFL);
            int i = (int) ((serverId >> 48) & 0x7FFFL);
            StringBuilder sb = new StringBuilder(48);
            sb.append(((h >> 24) & 0xFF)).append('.');
            sb.append(((h >> 16) & 0xFF)).append('.');
            sb.append(((h >> 8) & 0xFF)).append('.');
            sb.append((h & 0xFF));
            sb.append(SEP).append(p);
            sb.append(SEP).append(i);
            sb.append(SEP).append(identity);
            sb.append(SEP);
            //简单的方法补齐 '0'
            if (validation < 10) {
                sb.append(ZERO, 0, 7);
            }
            else if (validation < 100) {
                sb.append(ZERO, 0, 6);
            }
            else if (validation < 1000) {
                sb.append(ZERO, 0, 5);
            }
            else if (validation < 10000) {
                sb.append(ZERO, 0, 4);
            }
            else if (validation < 100000) {
                sb.append(ZERO, 0, 3);
            }
            else if (validation < 1000000) {
                sb.append(ZERO, 0, 2);
            }
            else if (validation < 10000000) {
                sb.append(ZERO, 0, 1);
            }
            sb.append(validation);
            str = sb.toString();
        }
        return str;
    }

    /**
     * 克隆
     *
     * @see Cloneable
     */
    public Object clone()
    {
        SimpleSessionID sid = null;
        try {
            sid = (SimpleSessionID) super.clone();
        }
        catch (CloneNotSupportedException cnse) {
        }
        return sid;
    }

    /**
     * 返回校验数字
     *
     * @return 校验数字
     */
    public int getValidation()
    {
        return validation;
    }

    @Override
    public void writeExternal(ObjectOutput oos) throws IOException {
        oos.writeLong(serverId);
        oos.writeInt(identity);
        oos.writeInt(validation);
    }

    @Override
    public void readExternal(ObjectInput ois) throws IOException, ClassNotFoundException {
        serverId = ois.readLong();
        identity = ois.readInt();
        validation = ois.readInt();
    }
}
