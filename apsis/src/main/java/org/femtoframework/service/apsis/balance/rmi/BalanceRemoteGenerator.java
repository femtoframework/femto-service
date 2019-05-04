package org.femtoframework.service.apsis.balance.rmi;

import org.femtoframework.service.apsis.balance.BalanceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.femtoframework.coin.remote.RemoteGenerator;

/**
 * 负载均衡远程引用产生器
 *
 * @author fengyun
 * @version 1.00 2005-8-5 15:56:07
 */
public class BalanceRemoteGenerator implements RemoteGenerator
{
    private Logger log = LoggerFactory.getLogger("apsis/balance/remote/generator");

    /**
     * 根据接口定义和远程URI来产生远程对象
     *
     * @param expectedType 期望类型
     * @param interfaces   接口数组
     * @param uri          对象定位
     * @return 产生的对象
     */
    public Object generate(String expectedType, String uri, String... interfaces)
    {
        Object obj = null;
        try {
            obj = BalanceUtil.generate(expectedType, uri, interfaces);
        }
        catch (Exception e) {
            log.warn("Generate type:" + expectedType + " uri:" + uri, e);
        }
        return obj;
    }
}
