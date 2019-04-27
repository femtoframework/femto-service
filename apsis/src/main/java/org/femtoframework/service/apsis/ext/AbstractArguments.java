package org.femtoframework.service.apsis.ext;


import org.femtoframework.io.DataCodec;
import org.femtoframework.net.comm.AddressAware;
import org.femtoframework.parameters.Parameters;
import org.femtoframework.parameters.ParametersMap;
import org.femtoframework.service.*;
import org.femtoframework.service.apsis.SessionLocal;
import org.femtoframework.service.apsis.SimpleSessionID;
import org.femtoframework.service.apsis.naming.ApsisName;
import org.femtoframework.util.CollectionUtil;
import org.femtoframework.util.StringUtil;
import org.femtoframework.util.i18n.LocaleUtil;

import javax.naming.Name;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

/**
 * 抽象的Arguments
 *
 * @author fengyun
 * @version 1.00 2005-9-19 22:08:33
 */
public abstract class AbstractArguments extends AbstractMap<String, Object>
    implements Arguments, Externalizable, SessionContextAware, AddressAware {
    /**
     * 请求发送者的主机
     */
    private transient String host;

    /**
     * 请求发送者的端口
     */
    private transient int port;

    /**
     * 会话标识
     */
    private SessionID sessionId = null;

    /**
     * 会话上下文
     */
    private transient SessionContext sessionContext;

    /**
     * Action
     */
    private String action;

    /**
     * Locale
     */
    private Locale locale = Locale.getDefault();

    /**
     * 参数存储
     */
    private ParametersMap<Object> parameters;

    /**
     * 请求名称
     */
    private Name requestName;


    private int timeout;


    public AbstractArguments() {
    }

    /**
     * 构造
     *
     * @param requestName 请求名称
     */
    public AbstractArguments(String requestName) {
        Name name = new ApsisName(requestName);
        if (name.size() != 3) {
            throw new IllegalArgumentException("Invalid request name:" + requestName);
        }
        this.requestName = name;
        this.parameters = new ParametersMap<>();

        //获取当前线程的绑带会话标识
        this.sessionId = SessionLocal.getSessionID();
    }

    /**
     * 返回对象型参数
     *
     * @param key 键值
     * @return 如果找不到返回是<code>#DEFAULT_OBJECT</code>
     */
    public Object get(Object key) {
        return parameters.get(key);
    }

    /**
     * 设置对象型参数
     *
     * @param key   键值
     * @param value 对象型
     */
    public Object put(String key, Object value) {
        return parameters.put(key, value);
    }

    /**
     * 返回所有的参数名称
     *
     * @return 参数名称枚举
     */
    public Set<String> keySet() {
        return CollectionUtil.keySet(parameters);
    }

    /**
     * 返回各种数据类型对象
     *
     * @return 对象枚举
     */
    public Collection<Object> values() {
        return CollectionUtil.valueSet(parameters);
    }


    /**
     * Entry Set
     *
     * @return Entry Set
     */
    public Set<Entry<String, Object>> entrySet() {
        return CollectionUtil.entrySet(parameters);
    }

    /**
     * 数目
     */
    public int size() {
        return parameters != null ? parameters.size() : 0;
    }

    /**
     * 清除所有的参数
     */
    public void clear() {
        if (parameters != null) {
            parameters.clear();
        }
    }

    /**
     * 删除参数
     *
     * @param name 删除参数
     * @return 如果存在该名称的参数，返回删除的参数；否则返回<code>null</code>
     */
    public Object remove(Object name) {
        return parameters != null ? parameters.remove(name) : null;
    }

    private static final int PART_SERVER = 0;
    private static final int PART_NAMESPACE = 1;
    private static final int PART_COMPONENT = 2;

    /**
     * 返回要访问的服务器信息，可以是统配符或者是绝对地址
     *
     * @return 访问的的服务器信息
     */
    public String getServerName() {
        return requestName.get(PART_SERVER);
    }

    /**
     * 返回要访问的服务名称
     *
     * @return 需要访问的服务名称
     */
    public String getNamespace() {
        return requestName.get(PART_NAMESPACE);
    }

    /**
     * 返回访问的Model名称
     *
     * @return Model名称
     */
    public String getComponentName() {
        return requestName.get(PART_COMPONENT);
    }

    /**
     * 返回访问的Model名称
     * <p/>
     * Model定位符，用来唯一定位命令<br>
     * <p/>
     * <p/>
     * <pre>
     * 1. 说明:
     *
     * Server         服务器
     * Application    应用（是对服务器的会话）
     * Service        服务
     * Session        会话（是对服务的会话）
     * Model          组件
     * Command        命令
     * Request        请求
     * Response       响应
     *
     * 2. 定义:
     *
     * "*"        --> 所有的
     * "+"        --> 其中任一个
     * "$"        --> 当前服务器
     * "/"        --> 分隔符
     *  %         --> 所有远程服务器
     *  @         --> 优先选择本地的服务器
     *  #type     --> 指定类型的服务器
     *  =selectorType  --> 指定采用什么样的选取器（random round_robin hash)
     *  !host:port     --> 表示向指定的主机和端口的服务器发送
     *
     * 3. 变量：
     * server     --> 服务器名
     * service    --> 服务名
     * model      --> 组件名
     *
     * 4. 例子：
     * 服务器:           "server"
     * 服务:             "server/service"
     * 组件:             "server/service/model"
     *
     * 所有服务器:               "*"
     * 任选一个服务器中的服务：  "+/service"
     * 访问当前服务器中的服务：  "$/service"
     * </pre>
     *
     * @return 请求名称
     */
    public Name getRequestName() {
        return requestName;
    }

    /**
     * 返回Session标识
     *
     * @return [String] Session标识
     */
    public SessionID getSessionID() {
        return sessionId;
    }

    /**
     * 设置会话标识(用于找到服务器会话和服务会话）
     *
     * @param sessionId 会话标识
     */
    public void setSessionID(String sessionId) {
        setSessionID(sessionId == null ? null : new SimpleSessionID(sessionId));
    }

    /**
     * 设置会话标识(用于找到服务器会话和服务会话）
     *
     * @param sessionId 会话标识
     */
    public void setSessionID(SessionID sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * 返回<code>java.util.Locale</code>
     *
     * @return Locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * 设置区域信息
     *
     * @param locale <code>java.util.Locale</code>
     */
    public void setLocale(Locale locale) {
        setLocale(locale.toString());
    }

    /**
     * 设置区域信息
     *
     * @param locale 区域信息
     */
    public void setLocale(String locale) {
        this.locale = LocaleUtil.getLocaleByLanguage(locale);
    }

    /**
     * 设置请求action
     *
     * @param action Action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * 返回请求Action
     *
     * @return Action
     */
    public String getAction() {
        return action;
    }

    /**
     * 设置请求超时时间
     *
     * @param timeout 超时时间
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * 返回请求超时时间，如果请求超时时间是0，表示没有超时
     *
     * @return 请求超时时间
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * 创建当前用户在当前服务中的Session，它与getSession(true)不同的是它不把创建的会话保存为自身使用的会话
     *
     * @return [Session] 创建的会话
     */
    public Session createSession() {
        return sessionContext.createSession();
    }

    /**
     * 返回当前用户在当前服务中的Session
     *
     * @return [Session] 当前用户会话
     */
    public Session getSession() {
        return sessionContext.getSession();
    }

    /**
     * 返回当前用户在当前服务中的Session
     *
     * @param create 如果<code>true</code>，当<code>session==null</code>时建会话
     * @return [Session] 当前用户会话
     */
    public Session getSession(boolean create) {
        return sessionContext.getSession(create);
    }

    /**
     * 结束当前用户在当前服务中的Session
     */
    public void endSession() {
        sessionContext.endSession();
    }


    /**
     * 输出对象
     *
     * @param oos 对象输出流
     */
    public void writeExternal(ObjectOutput oos)
        throws IOException {
        oos.writeInt(timeout);
        DataCodec.writeSingle(oos, sessionId != null ? sessionId.toString() : null);
        DataCodec.writeSingle(oos, action);
        DataCodec.writeSingle(oos, locale.toString());
        DataCodec.writeSingle(oos, requestName.toString());
        parameters.writeExternal(oos);
    }


    /**
     * 对象输入流
     *
     * @param ois 对象输入流
     */
    public void readExternal(ObjectInput ois)
        throws IOException, ClassNotFoundException {
        timeout = ois.readInt();
        String sid = DataCodec.readSingle(ois);
        if (StringUtil.isValid(sid)) {
            setSessionID(sid);
        }
        action = DataCodec.readSingle(ois);
        locale = LocaleUtil.getLocale(DataCodec.readSingle(ois));
        requestName = new ApsisName(DataCodec.readSingle(ois));
        if (parameters == null) {
            parameters = new ParametersMap<>();
        }
        parameters.readExternal(ois);
    }

    /**
     * 返回会话上下文
     *
     * @return 会话上下文
     */
    public SessionContext getSessionContext() {
        return sessionContext;
    }

    /**
     * 设置会话上下文
     *
     * @param sessionContext 会话上下文
     */
    public void setSessionContext(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Arguments [");
        sb.append(action).append(',');
        sb.append(locale.toString().toLowerCase()).append(',');
        sb.append(requestName).append(',');
        sb.append(sessionId).append("] ");
        sb.append(parameters.toString());
        return sb.toString();
    }

    /**
     * 返回请求发送者主机地址
     *
     * @return 远程主机
     */
    public String getRemoteHost() {
        return host;
    }

    /**
     * 返回请求发送者主机端口
     *
     * @return 远程端口
     */
    public int getRemotePort() {
        return port;
    }

    /**
     * 设置主机地址
     *
     * @param host 主机地址
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 设置端口
     *
     * @param port 端口
     */
    public void setPort(int port) {
        this.port = port;
    }


    /**
     * 返回实际的参数集合
     *
     * @return 实际的参数集合
     */
    protected Parameters<Object> getParameters() {
        return parameters;
    }
}
