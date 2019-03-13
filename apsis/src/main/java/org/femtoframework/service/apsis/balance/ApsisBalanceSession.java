package org.femtoframework.service.apsis.balance;

import org.femtoframework.parameters.Parameters;
import org.femtoframework.service.SessionID;
import org.femtoframework.service.apsis.ApsisSessionID;
import org.femtoframework.service.apsis.session.ApsisSession;
import org.femtoframework.service.balance.BalanceSession;

import javax.security.auth.Subject;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

/**
 * Apsis 负载均衡会话
 *
 * @author fengyun
 * @version 1.00 2005-11-17 10:17:45
 */
public class ApsisBalanceSession extends AbstractMap<String, Object>
    implements BalanceSession, Externalizable, Parameters<Object>
{
    private Map<String, ApsisSessionID> map = new HashMap<>(8);

    private transient Subject subject;

    protected SessionID sid = null;
    private static final int DEFAULT_TIMEOUT = 1800000;
    private long startingTime;
    private long lastAccessedTime;
    private int timeout = DEFAULT_TIMEOUT;

    public ApsisBalanceSession(SessionID sid) {
        this.sid = sid;
        this.startingTime = System.currentTimeMillis();
        this.lastAccessedTime = startingTime;
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

        sid = null;
    }

    /**
     * 更新最后访问时间
     */
    public void access() {
        this.lastAccessedTime = System.currentTimeMillis();
    }

    /**
     * 返回当前会话对应的Subject
     *
     * @return 当前的Subject
     */
    public Subject getSubject() {
        if (subject == null) {
            //TODO
//            subject = AuthUtil.getSubject();
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

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return null;
    }
}
