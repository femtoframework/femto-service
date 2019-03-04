package org.femtoframework.service.apsis.balance.ext;

import org.femtoframework.parameters.Parameters;
import org.femtoframework.service.ServerID;
import org.femtoframework.service.SessionID;
import org.femtoframework.service.StatefulElement;
import org.femtoframework.service.apsis.*;
import org.femtoframework.service.apsis.balance.BalanceSessionGenerator;
import org.femtoframework.service.apsis.client.MultiClient;
import org.femtoframework.service.balance.BalanceSession;
import org.femtoframework.service.client.ClientList;
import org.femtoframework.service.client.ClientUtil;
import org.femtoframework.util.CollectionUtil;
import org.femtoframework.util.DataUtil;
import org.femtoframework.util.StringUtil;
import org.femtoframework.util.selector.ListSelector;
import org.femtoframework.util.selector.ListSelectorUtil;
import org.femtoframework.util.status.StatusException;
import org.slf4j.Logger;

import javax.naming.Name;
import javax.security.auth.Subject;
import java.util.List;
import java.util.Set;

import static org.femtoframework.service.apsis.balance.BalanceServerID.EXTENSION;

/**
 * Apsis 负载均衡器
 *
 * @author fengyun
 * @version 1.00 2005-8-6 0:35:14
 */
public class SimpleBalancer implements ApsisBalancer {
    /**
     * ApsisClientMap
     */
    private ApsisClientManager manager;

    /**
     * 本地客户端
     */
    private ApsisClient localClient = null;

    /**
     * 会话容器
     */
    private SessionContainer<BalanceSession> container = null;

    /**
     * 会话产生器
     */
    private SessionGenerator generator = null;

    private Logger logger = LogUtil.getLogger("apsis/balancher");

    private ApsisServiceTree serviceTree = null;

    public SimpleBalancer(ApsisClientManager manager) {
        this.manager = manager;
        serviceTree = ApsisServerUtil.getServiceTree(manager);
    }

    /**
     * 返回会话容器
     *
     * @return 会话容器
     */
    private SessionContainer<BalanceSession> getSessionContainer() {
        if (container == null) {
            container = new SimpleSessionContainer<BalanceSession>();
        }
        return container;
    }

    /**
     * 返回会话产生器
     *
     * @return 会话产生器
     */
    private SessionGenerator getSessionGenerator() {
        if (generator == null) {
            generator = new BalanceSessionGenerator();
        }
        return generator;
    }

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
    public ApsisClient apsisBalance(Name rule) {
        return apsisBalance(rule, null);
    }

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
     *  !host:port             表示向指定的主机和端口的服务器发送
     * </pre>
     *
     * @param rule       负载均衡规则
     * @param parameters 负载均衡参数集合
     * @return 客户端
     */
    public ApsisClient apsisBalance(Name rule, Parameters parameters) {
        StatefulElement element = parameters instanceof StatefulElement ? (StatefulElement)parameters : null;
        try {
            return apsisBalance(rule, null, element, parameters);
        }
        catch (StatusException e) {
            //Never
            return null;
        }
    }

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
    public ApsisClient apsisBalance(Name rule, ApsisSessionID sid,
                                    StatefulElement element,
                                    Parameters parameters)
        throws StatusException {
        if (rule == null || rule.isEmpty()) {
            throw new IllegalArgumentException("Illegal name:" + rule);
        }
        //如果拥有会话标识，那么采用相应的标识进行负载均衡
        String server = rule.get(0);
        if (!ApsisName.isSpecialPrefix(server)) {
            server = "#" + server;
        }
        String service = rule.size() > 1 ? rule.get(1) : null;
        boolean hasService = StringUtil.isValid(service);
        if (sid != null) {
            ApsisSessionID backEndSid = null;
            int extension = sid.getExtension();
            if (extension == EXTENSION) {
                //负载均衡客户端会话
                BalanceSession session = getSessionContainer().getSession(sid);
                if (session != null) {
                    session.access();
                    if (server.startsWith("#")) {
                        backEndSid = (ApsisSessionID)session.getSessionID(server.substring(1));
                    }
                    else {
                        backEndSid = (ApsisSessionID)session.getFirstSessionID();
                    }
                }
            }
            else {
                backEndSid = sid;
            }
            if (element != null) {
                element.setSessionID(backEndSid);
            }
            if (backEndSid != null) {
                //自动填充 Subject
// 已经创建了会话的，不再固定传送 Subject
//                setSubject(parameters);
                ApsisClient client = manager.getClient(backEndSid.getServerId());
                if (client != null) {
                    return client;
                }
                else {
                    if (logger.isInfoEnabled()) {
                        logger.info(
                            "Can't find the client " + backEndSid + " " + backEndSid.getServerId() + " in the map" + manager);
                    }
                }
            }
        }
        ApsisClient local = getLocalClient();
        if (server == null || "+".equals(server)) {
            //任意选择一个服务器
            if (hasService) {
                return serviceTree.findClient(service);
            }
            return getNextClient();
        }
        else if ("*".equals(server) || "%".equals(server) || "^".equals(server)) {
            //所有服务器
            boolean removeLocal = "%".equals(server);
            List<ApsisClient> clients;
            if (hasService) {
                ClientList<ApsisClient> list = serviceTree.getClientList(service);
                if (list == null) {
                    return null;
                }
                if (removeLocal) {
                    list.removeClient(local);
                }
                clients = list.getAll();
            }
            else {
                if (removeLocal) {
                    List<ApsisClient> list = manager.getClients();
                    List<ApsisClient> newList = CollectionUtil.clone(list);
                    newList.remove(local);
                    clients = newList;
                }
                else {
                    return ClientUtil.getClient("*", 0, true);
                }
            }
            if (clients != null && !clients.isEmpty()) {
                return new MultiClient(clients);
            }
            return null;
        }
        else if ("@".equals(server)) {
            //优先选择本地的服务器
            if (hasService) {
                ClientList<ApsisClient> list = serviceTree.getClientList(service);
                return list == null ? null :
                       list.containsClient(local) ? local : serviceTree.findClient(service);
            }
            return local;
        }
        else if ("$".equals(server)) {
            //选择本地服务器
            return local;
        }
        else if (server.startsWith("=")) {
            //指定采用什么样的选取器（random round_robin hash)
            String ruleType = server.substring(1);
            ListSelector listSelector = ListSelectorUtil.createSelector(ruleType);
            return listSelector.select(manager.getClients(), parameters);
        }
        else if (server.startsWith("#")) {
            //选择指定类型的服务器
            String serverType = server.substring(1);
            setSubject(parameters);
            return getNextClientByServerType(serverType);
        }
        else if (server.startsWith("!")) {
            //!host:port表示向指定的主机和端口的服务器发送
            int i = server.indexOf(':', 1);
            if (i > 1) {
                String host = server.substring(1, i);
                int port = DataUtil.getInt(server.substring(i + 1), -1);
                if (port != -1) {
                    return manager.getClient(host, port);
                }
            }
            throw new IllegalArgumentException("Illegal rule:" + rule);
        }
        else {
            setSubject(parameters);
            return getNextClientByServerType(server);
        }
//        throw new IllegalArgumentException("Illegal rule:" + rule);
    }

    /**
     * 自动填充Subject
     *
     * @param parameters 请求
     */
    private void setSubject(Parameters parameters) {
        authRequired(null, parameters);
    }

    /**
     * 负载均衡回调
     *
     * @param rule      服务器规则
     * @param sid       请求的
     * @param element   有状态的请求
     * @param returnSid 返回的sessionID
     */
    public void apsisBalanceHook(Name rule, ApsisSessionID sid,
                                 StatefulElement element, SessionID returnSid) {
        String server = rule.get(0);
        if (!server.startsWith("#")) { // just process while specified server
            if (ApsisName.isSpecialPrefix(server)) {
                return;
            }
        }
        String realName = server.startsWith("#") ? server.substring(1) : server;
        if (returnSid != null) {
            BalanceSession session = null;
            //判断请求中的会话标识是否已经存在
            if (sid != null && sid.getExtension() == EXTENSION) {
                //负载均衡客户端会话
                session = getSessionContainer().getSession(sid);
            }
            if (session == null) {
                //产生新的BalanceSession
                if (!(sid != null && sid.getExtension() == EXTENSION)) {
                    sid = null;
                }
                Parameters parameters = element instanceof Parameters ? (Parameters)element : null;
                session = (BalanceSession)getSessionGenerator().generate(sid, parameters);
                getSessionContainer().addSession(session);
            }
            session.access();
            session.setSessionID(realName, returnSid);
            if (element != null) {
                element.setSessionID(session.getSessionID());
            }
        }
    }

    /**
     * 处理需要认证的请求
     *
     * @param element    状态项
     * @param parameters 参数集
     */
    public void authRequired(StatefulElement element, Parameters parameters) {
        //TODO
//        if (parameters != null && parameters.getObject(ApsisConstants.SUBJECT) == null) {
//            Subject subject = AuthUtil.getSubject();
//            if (subject != null) { //从Subject中获取User对象，当第一次创建会话的时候传递User对象给相应的服务器
//                AutzPrincipal principal = AutzPrincipal.getUserAutz(subject);
//                if (principal != null) {
//                    Set set = subject.getPublicCredentials();
//                    if (!set.isEmpty()) {
//                        int id = principal.getId();
//                        for (Object o : set) {
//                            if (o instanceof Identified && id == ((Identified)o).getId()) {
//                                //用户对象
//                                parameters.setObject(ApsisConstants.USER, o);
//                                break;
//                            }
//                        }
//                    }
//                }
//                parameters.setObject(ApsisConstants.SUBJECT, subject);
//            }
//        }
    }

    /**
     * 返回本地客户端
     *
     * @return 本地客户端
     */
    public ApsisClient getLocalClient() {
        if (localClient == null) {
            localClient = manager.getClient(ServerID.getLocal().getId());
        }
        return localClient;
    }

    /**
     * 选取其中的一个客户端
     *
     * @return 客户端
     */
    public ApsisClient getNextClient() {
        List<ApsisClient> clients = manager.getClients();
        synchronized (clients) {
            if (clients.size() > 0) {
                try {
                    return clients.get(UniqueID.nextInt(clients.size()));
                }
                catch (Exception e) {
                    try {
                        return clients.get(0);
                    }
                    catch (Exception ex) {
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据应用类型选取客户端
     *
     * @param serverType
     * @return 客户端
     */
    private ApsisClient getNextClientByServerType(String serverType) {
        ClientList clients = manager.getClients(serverType);
        if (clients != null) {
            return (ApsisClient)clients.findClient();
        }
        return null;
    }

    /**
     * 选取其中的一个客户端
     *
     * @param rule 负载均衡规则
     * @return 客户端
     */
    public ApsisClient balance(String rule) {
        return balance(rule, null);
    }

    /**
     * 选取其中的一个客户端
     *
     * @param rule       负载均衡规则
     * @param parameters 负载均衡参数集合
     * @return 客户端
     */
    public ApsisClient balance(String rule, Parameters parameters) {
        return apsisBalance(new ApsisName(rule), parameters);
    }
}
