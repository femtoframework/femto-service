package org.femtoframework.service.apsis.balance;

import org.femtoframework.service.ServerID;

/**
 * 负载均衡的服务器标识
 *
 * @author fengyun
 * @version 1.00 2005-11-17 10:27:26
 */
public class BalanceServerID
{
    /**
     * 扩展标识
     */
    public static final int EXTENSION = 0xFF;

    private static ServerID local;

    /**
     * 返回用于负载均衡的服务器标识
     *
     * @return 服务器标识
     */
    public static ServerID getLocal()
    {
        if (local != null) {
            return local;
        }
        ServerID sid = ServerID.getLocal();
        local = new ServerID(sid.getHost(), sid.getPort(), EXTENSION);
        return local;
    }

    /**
     * 返回用于负载均衡的服务器标识数字
     *
     * @return 服务器标识数字
     */
    public static long getServerId()
    {
        return getLocal().getId();
    }
}
