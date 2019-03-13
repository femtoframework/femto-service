package org.femtoframework.service.apsis.coin;

import org.femtoframework.naming.NamingService;
import org.femtoframework.naming.NamingServiceFactory;
import org.femtoframework.net.HostPort;
import org.femtoframework.service.rmi.RemoteException;
import org.femtoframework.service.rmi.server.StubUtil;

import javax.naming.NamingException;
import java.net.URI;

/**
 * Coin命名服务工厂
 *
 * @author fengyun
 * @version 1.00 2005-6-4 22:24:09
 */
public class CoinNamingServiceFactory implements NamingServiceFactory
{
    private CoinNamingService local;

    /**
     * 返回本地的该Scheme的命名服务
     *
     * @return 本地该Scheme的命名服务
     */
    public NamingService getLocalService()
    {
        if (local == null) {
            synchronized (this) {
                if (local == null) {
                    CoinNamingService ns = new CoinNamingService();
                    ns.start();
                    this.local = ns;
                }
            }
        }
        return local;
    }

    /**
     * 根据主机名和端口创建名字服务
     *
     * @param host 主机
     * @param port 端口
     * @return 名字服务
     * @throws NamingException 如果名字服务不存在或者连接异常等
     */
    public NamingService createService(String host, int port) throws NamingException
    {
        if (HostPort.isLocal(host, port)) {
            return getLocalService();
        }
        else {
            HostPort hostPort = new HostPort(host, port);
            try {
                return (NamingService) StubUtil.createStub(CoinNamingService.class,
                                                           hostPort.getId(), CoinNamingConstants.OBJ_ID);
            }
            catch (RemoteException re) {
                NamingException ne = new NamingException("Remote Naming Service exception");
                ne.setRootCause(re);
                throw ne;
            }
        }
    }

    /**
     * 根据主机名和端口创建名字服务
     *
     * @param uri 名字服务统一资源定位
     * @return 名字服务
     * @throws NamingException 如果名字服务不存在或者连接异常等
     */
    public NamingService createService(URI uri) throws NamingException
    {
        return createService(uri.getHost(), uri.getPort());
    }
}
