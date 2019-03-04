package org.femtoframework.service.apsis;

import org.femtoframework.parameters.Parameters;
import org.femtoframework.service.SessionID;
import org.femtoframework.service.StatefulElement;
import org.femtoframework.service.client.Balancer;
import org.femtoframework.util.status.StatusException;

import javax.naming.Name;

/**
 * 负载均衡
 *
 * @author fengyun
 * @version 1.00 2005-8-5 23:58:39
 */
public interface ApsisBalancer extends Balancer<ApsisClient>
{
    /**
     * 选取其中的一个客户端
     * <pre>
     * 通配符说明：
     *  +              任选一个服务器
     *  *              所有服务器
     *  $              本地服务器
     *  %              所有远程服务器
     *  @              优先选择本地的服务器
     *  #serverType    选择指定类型的服务器
     *  =balanceType  指定采用什么样的选取器（random round_robin hash)
     *  !host:port     表示向指定的主机和端口的服务器发送
     * </pre>
     *
     * @param rule 负载均衡规则
     * @return 客户端
     */
    ApsisClient apsisBalance(Name rule);

    /**
     * 选取其中的一个客户端
     * <pre>
     * 通配符说明：
     *  +              任选一个服务器
     *  *              所有服务器
     *  $              本地服务器
     *  %              所有远程服务器
     *  @              优先选择本地的服务器
     *  #serverType    选择指定类型的服务器
     *  =balanceType  指定采用什么样的选取器（random round_robin hash)
     *  !host:port     表示向指定的主机和端口的服务器发送
     * </pre>
     *
     * @param rule       负载均衡规则
     * @param parameters 负载均衡参数集合
     * @return 客户端
     */
    ApsisClient apsisBalance(Name rule, Parameters parameters);

    /**
     * 选取其中的一个客户端
     * <pre>
     * 通配符说明：
     *  +              任选一个服务器
     *  *              所有服务器
     *  $              本地服务器
     *  %              所有远程服务器
     *  @              优先选择本地的服务器
     *  #serverType    选择指定类型的服务器
     *  =balanceType  指定采用什么样的选取器（random round_robin hash)
     *  !host:port     表示向指定的主机和端口的服务器发送
     * </pre>
     *
     * @param rule       负载均衡规则
     * @param sid        会话标识
     * @param element    有状态的数据集合
     * @param parameters 负载均衡参数集合
     * @return 客户端
     * @throws StatusException 状态消息
     */
    ApsisClient apsisBalance(Name rule, ApsisSessionID sid,
                                    StatefulElement element, Parameters parameters)
        throws StatusException;

    /**
     * 负载均衡回调
     *
     * @param rule 服务器规则
     * @param sid  请求的
     */
    void apsisBalanceHook(Name rule, ApsisSessionID sid, StatefulElement element, SessionID returnSid);


    /**
     * 处理需要认证的请求
     *
     * @param element    状态项
     * @param parameters 参数集
     */
    void authRequired(StatefulElement element, Parameters parameters);
}
