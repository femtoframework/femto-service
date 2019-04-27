package org.femtoframework.service.apsis.session;

import org.femtoframework.parameters.ParametersMap;
import org.femtoframework.service.Session;
import org.femtoframework.service.SessionID;
import org.femtoframework.service.apsis.session.SessionContainer;
import org.femtoframework.service.apsis.session.SessionContainerAware;

import javax.security.auth.Subject;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Apsis服务器会话实现
 * 支持内部保留多个service会话
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-7-25 16:09:46
 */
public class ApsisSession extends ParametersMap<Object>
        implements Session, SessionContainerAware
{
    private transient SessionContainer container = null;
    private Subject subject;

    protected SessionID sid;
    private static final int DEFAULT_TIMEOUT = 1800000;
    private long startingTime;
    private long lastAccessedTime;
    private int timeout = DEFAULT_TIMEOUT;

    /**
     * 输出对象
     *
     * @param oos 对象输出流
     */
    public void writeExternal(ObjectOutput oos) throws IOException
    {
        oos.writeObject( sid);
        oos.writeLong(startingTime);
        oos.writeLong(lastAccessedTime);
        oos.writeInt(timeout);
        oos.writeObject( subject);
    }

    /**
     * 对象输入流
     *
     * @param ois 对象输入流
     */
    public void readExternal(ObjectInput ois) throws IOException, ClassNotFoundException
    {
        sid = (SessionID) ois.readObject();
        startingTime = ois.readLong();
        lastAccessedTime = ois.readLong();
        timeout = ois.readInt();

        subject = (Subject) ois.readObject();
    }

    /**
     * 注入SessionContainer
     *
     * @param container SessionContainer
     */
    public void setSessionContainer(SessionContainer container)
    {
        if (container == null) {
            if (this.container != null) {
                fireDestroyedEvent();
            }
        }
        else {
            this.container = container;
        }
    }

    /**
     * 发送DestroyedEvent
     */
    public void fireDestroyedEvent()
    {
    }

    /**
     * 返回当前会话对应的Subject
     *
     * @return 当前的Subject
     */
    public Subject getSubject()
    {
        return subject;
    }

    /**
     * 设置认证主题
     *
     * @param subject 认证主题
     */
    public void setSubject(Subject subject)
    {
        this.subject = subject;
    }



    public ApsisSession(SessionID sid)
    {
        super();
        this.sid = sid;
        this.startingTime = System.currentTimeMillis();
        this.lastAccessedTime = startingTime;
    }

    /**
     * 返回会话标识
     */
    public SessionID getSessionID()
    {
        return this.sid;
    }

    /**
     * 返回字符串形式的会话标识
     */
    public String getId()
    {
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
    public long getStartingTime()
    {
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
    public long getLastAccessedTime()
    {
        return lastAccessedTime;
    }

    /**
     * 返回有效时间（单位：毫秒）
     *
     * @return 有效时间
     */
    public int getTimeout()
    {
        return timeout;
    }

    /**
     * 设置超时时间
     *
     * @param timeout 有效时间
     */
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    /**
     * 判断会话是否超时
     *
     * @return 超时返回<code>true</code>，否则返回<code>false</code>
     */
    public boolean isTimeout()
    {
        return System.currentTimeMillis() - lastAccessedTime > timeout;
    }

    /**
     * 结束会话（执行之后会话从池中删除）
     */
    public void expire()
    {
        super.clear();
        sid = null;
    }

    /**
     * 更新最后访问时间
     */
    public void access()
    {
        this.lastAccessedTime = System.currentTimeMillis();
    }

}
