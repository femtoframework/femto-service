package org.femtoframework.service.client;

import org.femtoframework.parameters.Parameters;
import org.femtoframework.service.Client;

/**
 * 负载均衡器
 *
 * @author fengyun
 * @version 1.00 2005-8-6 17:33:46
 */
public interface Balancer<C extends Client>
{
    /**
     * 选取其中的一个客户端
     *
     * @param rule 负载均衡规则
     * @return 客户端
     */
    C balance(String rule);

    /**
     * 选取其中的一个客户端
     *
     * @param rule       负载均衡规则
     * @param parameters 负载均衡参数集合
     * @return 客户端
     */
    C balance(String rule, Parameters parameters);
}
