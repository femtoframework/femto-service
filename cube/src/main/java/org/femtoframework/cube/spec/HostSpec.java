package org.femtoframework.cube.spec;

import org.femtoframework.bean.Initializable;
import org.femtoframework.bean.exception.InitializeException;
import org.femtoframework.util.DataUtil;
import org.femtoframework.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统中的一台主机
 *
 * @author fengyun
 * @version 1.00 2005-2-5 11:41:08
 */
public class HostSpec implements Initializable
{
    private String name;
    private transient String address;
    private String[] addresses;
    private List<String> servers = new ArrayList<>(2);

    /**
     * 返回主机名.
     *
     * @return 主机名
     */
    public String getName() {
        return name;
    }

    /**
     * 返回主机地址.
     *
     * @return 主机地址
     */
    public String getAddress() {
        return address;
    }

    /**
     * 返回主机地址，主机可用的IP地址，这个方法适合于一个主机在两个网段内，需要在不同网段内的服务进行通讯.
     * 多个IP地址的第一个作为服务器的唯一标识。
     *
     * @return 主机可用的IP地址
     */
    public String[] getAddresses() {
        return addresses;
    }

    /**
     * 返回所有服务器
     *
     * @return 所有服务器
     */
    public List<String> getServers() {
        return servers;
    }

    /**
     * 设置主机名
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 设置地址
     *
     * @param address 地址
     */
    public void setAddress(String address) {
        this.addresses = DataUtil.toStrings(address, ',');
        if (addresses == null || addresses.length == 0) {
            throw new InitializeException("No 'address' attribute");
        }
        this.address = addresses[0];
    }

    /**
     * 实际真正初始化
     */
    public void init() {
        if (StringUtil.isInvalid(name)) {
            throw new InitializeException("No 'name' attribute of a <host>");
        }
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }
}
