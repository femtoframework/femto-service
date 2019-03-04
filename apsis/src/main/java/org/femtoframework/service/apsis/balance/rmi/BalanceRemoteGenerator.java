package org.femtoframework.service.apsis.balance.rmi;

import org.bolango.apsis.balance.BalanceUtil;
import org.bolango.coin.DeploymentContext;
import org.bolango.coin.remote.RemoteGenerator;
import org.bolango.log.LogUtil;
import org.bolango.log.Logger;

/**
 * 负载均衡远程引用产生器
 *
 * @author fengyun
 * @version 1.00 2005-8-5 15:56:07
 */
public class BalanceRemoteGenerator implements RemoteGenerator
{

    private static final BalanceRemoteGenerator instance = new BalanceRemoteGenerator();

    /**
     * 构造
     */
    private BalanceRemoteGenerator()
    {
    }

    /**
     * 返回实例
     */
    public static BalanceRemoteGenerator getInstance()
    {
        return instance;
    }

    private Logger log = LogUtil.getLogger("apsis/balance/remote/generator");

    /**
     * 根据接口定义和远程URI来产生远程对象
     *
     * @param expectedType 期望类型
     * @param context      上下文
     * @param interfaces   接口数组
     * @param uri          对象定位
     * @return 产生的对象
     */
    public Object generate(String expectedType, DeploymentContext context, String[] interfaces, String uri)
    {
        Object obj = null;
        try {
            obj = BalanceUtil.generate(expectedType, interfaces, uri);
        }
        catch (Exception e) {
            log.warn("Generate type:" + expectedType + " uri:" + uri, e);
        }
        return obj;
    }
}
