package org.femtoframework.cube.ext;

import org.femtoframework.bean.Nameable;
import org.femtoframework.bean.exception.InitializeException;
import org.femtoframework.cube.TcpEndpoint;
import org.femtoframework.cube.util.AddressAllowBean;
import org.femtoframework.net.HostPort;
import org.femtoframework.net.InetAddressUtil;
import org.femtoframework.net.socket.bifurcation.BifurcatedEndpoint;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;

/**
 * Cube TCP接入点
 *
 * @author fengyun
 * @version 1.00 2005-3-14 19:58:28
 */
public class CubeTcpEndpoint extends BifurcatedEndpoint
    implements TcpEndpoint, Nameable
{

    private String name;

    public static final int MAX_TCP_PORT = 19168;

    /**
     * 允许访问控制
     */
    protected AddressAllowBean allow = new AddressAllowBean();

    /**
     * 初始化实现
     */
    public void _doInitialize() {
        setCheckLaunched(true);

        //Try the port
        HostPort.getLocalPort();
        //设置默认的配置
        InetAddress addr;
        if (getHost() == null) {
            String address = System.getProperty("cube.system.address");
            if (address == null) {
                try {
                    addr = InetAddress.getLocalHost();
                    address = addr.getHostAddress();
                    System.setProperty("cube.system.address", address);
                    System.setProperty("cube.system.addresses", address);
                    setAddress(address);
                }
                catch (UnknownHostException e) {
                }
            }
            else {
                try {
                    addr = InetAddress.getByName(address);
                }
                catch (UnknownHostException e) {
                    throw new InitializeException("Unknown host:" + address);
                }
                //判断是不是本地的IP地址
                if (!InetAddressUtil.isLocalAddress(addr)) {
                    throw new InitializeException("The ip address can't be found in any network interface:" + address);
                }
                setAddress(address);
            }
        }
        if (getPort() == 0) {
            int p = HostPort.getLocalPort();
            System.setProperty("cube.system.port", String.valueOf(p));
            setPort(p);
        }
        String allow = System.getProperty("cube.system.allow");
        if (allow != null && getAllow() == null) {
//            setAllow(allow);
            this.allow.setAllow(allow);
        }
        else {
            setAllow(AddressAllowBean.getLocalNetwork());
        }
        setSocketHandlerFactory(new CubeSocketHandlerFactory());
        super._doInitialize();
    }


//    /**
//     * 设置简单的允许访问的IP地址列表
//     *
//     * @param allow 允许访问的IP地址列表（采用';'分隔支持'*','?'统配符的IP地址）
//     */
//    public void setAllow(String allow) {
//        this.allow.setAllow(allow);
//    }

    /**
     * 设置允许的地址表
     *
     * @param allow 允许地址表
     */
    public void setAllow(String[] allow) {
        this.allow.setAllow(allow);
    }

    /**
     * 返回允许访问的IP地址列表
     *
     * @return 允许访问的IP地址列表
     */
    public String[] getAllow() {
        return allow.getAllow();
    }

    /**
     * 处理Socket
     *
     * @param socket Socket
     */
    public void handle(Socket socket) {
        if (allow.isAllow(socket.getInetAddress())) {
            super.handle(socket);
        }
        else {
            if (log.isDebugEnabled()) {
                log.debug("Socket connection is not allow :" + socket);
            }
        }
    }

    /**
     * 设置线程执行器
     *
     * @param executor 线程执行器
     */
//    @ThreadPoolMeta (maxThreads = 8, minSpareThreads = 2, maxSpareThreads = 4, incThreads = 2)
    public void setExecutor(Executor executor) {
        super.setExecutor(executor);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
