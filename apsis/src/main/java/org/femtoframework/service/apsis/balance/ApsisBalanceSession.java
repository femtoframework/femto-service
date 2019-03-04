package org.femtoframework.service.apsis.balance;

import org.bolango.apsis.ApsisSession;
import org.bolango.apsis.ApsisSessionID;
import org.bolango.frame.Session;
import org.bolango.frame.SessionID;
import org.bolango.frame.balance.BalanceSession;
import org.bolango.security.auth.AuthUtil;
import org.bolango.util.AbstractParameters;
import org.bolango.util.CollectionUtil;

import javax.security.auth.Subject;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Apsis 负载均衡会话
 *
 * @author fengyun
 * @version 1.00 2005-11-17 10:17:45
 */
public class ApsisBalanceSession extends AbstractParameters
    implements BalanceSession, Externalizable, ApsisSession {
    private Map<String, ApsisSessionID> map = new HashMap<String, ApsisSessionID>(8);

    private transient Subject subject;

    public ApsisBalanceSession(SessionID sid) {
        this.sid = sid;
        this.startingTime = System.currentTimeMillis();
        this.lastAccessedTime = startingTime;
    }

    /**
     * 返回对象型参数
     *
     * @param key 键值
     * @return 如果找不到返回是<code>#DEFAULT_OBJECT</code>
     */
    public Object getObject(String key) {
        return null;
    }

    /**
     * 设置对象型参数
     *
     * @param key   键值
     * @param value 对象型
     */
    public Object setObject(String key, Object value) {
        return null;
    }

    /**
     * 返回所有的参数名称
     *
     * @return 参数名称枚举
     */
    public Iterator<String> getNames() {
        return CollectionUtil.EMPTY_ITERATOR;
    }

    /**
     * 删除参数
     *
     * @param name 删除参数
     * @return 如果存在该名称的参数，返回删除的参数；否则返回<code>null</code>
     */
    public Object remove(String name) {
        return null;
    }


    /**
     * 返回第一个会话，这个方法用于当没有声明相应的服务器类型的时候调用
     *
     * @return 第一个服务器会话
     */
    public SessionID getFirstSessionID() {
        if (map.isEmpty()) {
            return null;
        }
        else {
            return map.values().iterator().next();
        }
    }

    /**
     * 根据服务器类型找出对应的SessionID
     *
     * @param serverType 服务器类型
     * @return 会话标识
     */
    public SessionID getSessionID(String serverType) {
        return map.get(serverType);
    }

    /**
     * 添加SessionID和服务器类型的mapping
     *
     * @param serverType 服务器类型
     * @param sessionId  会话标识
     */
    public void setSessionID(String serverType, SessionID sessionId) {
        map.put(serverType, (ApsisSessionID)sessionId);
    }

    /**
     * 删除相应服务器类型的会话
     *
     * @param serverType 服务器类型
     * @return 相应的会话标示
     */
    public SessionID clearSessionID(String serverType) {
        return map.remove(serverType);
    }

    protected SessionID sid = null;
    private static final int DEFAULT_TIMEOUT = 1800000;
    private long startingTime;
    private long lastAccessedTime;
    private int timeout = DEFAULT_TIMEOUT;

    /**
     * 返回会话标识
     */
    public SessionID getSessionID() {
        return this.sid;
    }

    /**
     * 返回字符串形式的会话标识
     */
    public String getId() {
        return sid.toString();
    }

    public void release() {
        sid = null;
        map.clear();
    }

    /**
     * 返回会话创建时间
     *
     * @return a <code>long</code> specifying
     *         when this session was created,
     *         expressed in
     *         milliseconds since 1/1/1970 GMT
     */
    public long getStartingTime() {
        return startingTime;
    }

    /**
     * 返回最后访问时间
     *
     * @return 最后访问时间 a <code>long</code>
     *         representing the last time
     *         the client sent a request associated
     *         with this session, expressed in
     *         milliseconds since 1/1/1970 GMT
     */
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    /**
     * 返回有效时间（单位：毫秒）
     *
     * @return 有效时间
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * 设置超时时间
     *
     * @param timeout 有效时间
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * 判断会话是否超时
     *
     * @return 超时返回<code>true</code>，否则返回<code>false</code>
     */
    public boolean isTimeout() {
        return System.currentTimeMillis() - lastAccessedTime > timeout;
    }

    /**
     * 结束会话（执行之后会话从池中删除）
     */
    public void expire() {
        map.clear();
    }

    /**
     * 更新最后访问时间
     */
    public void access() {
        this.lastAccessedTime = System.currentTimeMillis();
    }

    /**
     * 返回更高一层的会话，如果是服务器会话，那么返回null，如果是服务会话，那么返回服务器会话
     *
     * @return 返回更高一层的会话
     */
    public Session getParent() {
        return null;
    }

    /**
     * 返回当前会话对应的Subject
     *
     * @return 当前的Subject
     */
    public Subject getSubject() {
        if (subject == null) {
            subject = AuthUtil.getSubject();
        }
        return subject;
    }

    /**
     * 设置认证主题
     *
     * @param subject 认证主题
     */
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    /**
     * 根据服务名称返回相应的会话，服务会话不可以拥有自己单独的
     *
     * @param serviceName 服务名称
     * @return 服务会话
     */
    public Session getServiceSession(String serviceName) {
        return null;
    }

    /**
     * 根据服务名称返回相应的会话，服务会话不可以拥有自己单独的
     *
     * @param serviceName 服务名称
     * @param create      如果服务不存在，是否创建相应的服务会话
     * @return 服务会话
     */
    public Session getServiceSession(String serviceName, boolean create) {
        return null;
    }

    /**
     * stop a service session
     *
     * @param serviceName ServiceName
     */
    public void endServiceSession(String serviceName) {

    }

    @Override
    public void writeExternal(ObjectOutput oos) throws IOException {
        oos.writeObject(sid);
        oos.writeLong(startingTime);
        oos.writeLong(lastAccessedTime);
        oos.writeInt(timeout);
    }

    @Override
    public void readExternal(ObjectInput ois) throws IOException, ClassNotFoundException {
        sid = (SessionID)ois.readObject();
        startingTime = ois.readLong();
        lastAccessedTime = ois.readLong();
        timeout = ois.readInt();
    }
}
