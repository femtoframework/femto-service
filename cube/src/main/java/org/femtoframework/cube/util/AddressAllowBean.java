package org.femtoframework.cube.util;

import org.femtoframework.net.InetAddressUtil;
import org.femtoframework.util.ArrayUtil;
import org.femtoframework.util.DataUtil;
import org.femtoframework.util.selector.SelectorUtil;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * IP地址允许访问的地址控制类
 *
 * @author fengyun
 */
public class AddressAllowBean
{
    /**
     * 允许访问的IP地址模式
     */
    private String[] allow;

    /**
     * char[][]
     */
    private char[][] pattern;

    /**
     * 本地地址集合
     */
    private Set<InetAddress> localAddrs = new HashSet<InetAddress>();

    {
        Collections.addAll(localAddrs, InetAddressUtil.getNetInterfaces());
    }

    /**
     * 返回本地网络的允许表（采用简单的C类地址规则）
     *
     * @return
     */
    public static String[] getLocalNetwork()
    {
        InetAddress[] addrs = InetAddressUtil.getNetInterfaces();
        if (addrs != null) {
            int len = addrs.length;
            String[] array = new String[len];
            String addr;
            int index;
            for (int i = 0; i < len; i++) {
                addr = addrs[i].getHostAddress();
                index = addr.lastIndexOf('.');
                array[i] = addr.substring(0, index + 1).concat("*");
            }
            return array;
        }
        return null;
    }


    /**
     * 设置简单的允许访问的IP地址列表
     *
     * @param allow 允许访问的IP地址列表（采用','分隔支持'*','?'统配符的IP地址）
     */
    public void setAllow(String allow)
    {
        if (allow == null) {
            throw new IllegalArgumentException("Null allow");
        }
        String[] array = DataUtil.toStrings(allow, ',');
        setAllow(array);
    }

    /**
     * 设置允许的地址表
     *
     * @param allow 允许地址表
     */
    public void setAllow(String[] allow)
    {
        if (allow != null) {
            this.allow = allow;
            int len = allow.length;
            this.pattern = new char[len][];
            for (int i = 0; i < len; i++) {
                pattern[i] = allow[i].toCharArray();
            }
        }
    }

    /**
     * 返回允许访问的IP地址列表
     *
     * @return
     */
    public String[] getAllow()
    {
        return allow;
    }

    /**
     * 检查是否允许远程的地址访问
     *
     * @param remote 远程地址
     * @return
     */
    public boolean isAllow(InetAddress remote)
    {
        if (isLocalAddress(remote)) {
            return true;
        }

        if (ArrayUtil.isValid(pattern)) {
            String remoteAddress = remote.getHostAddress();
            char[] chars = remoteAddress.toCharArray();
            for (int i = 0, len = pattern.length; i < len; i++) {
                if (SelectorUtil.match(pattern[i], chars)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 是否是本地的Socket
     *
     * @param remote 远程地址
     * @return
     */
    private boolean isLocalAddress(InetAddress remote)
    {
        return localAddrs.contains(remote);
    }
}
