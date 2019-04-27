package org.femtoframework.service.apsis.session;

import org.femtoframework.bean.AbstractLifecycle;
import org.femtoframework.bean.annotation.Property;
import org.femtoframework.service.Session;
import org.femtoframework.service.SessionEvent;
import org.femtoframework.service.SessionID;
import org.femtoframework.service.SessionListener;
import org.femtoframework.util.thread.ExecutorUtil;
import org.femtoframework.util.thread.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * ApsisSessionContainer
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-7-26 15:29:57
 */
public class ApsisSessionContainer<S extends Session>
    extends AbstractLifecycle
    implements SessionContainer<S>, Runnable
{
    private String name;

    /**
     * 构造
     */
    public ApsisSessionContainer() {
        this.name = "session";
        monitor = ExecutorUtil.newSingleThreadScheduler();
    }

    private static final int DEFAULT_TIMEOUT = 1800000;  //default to 30mins
    private static final int DEFAULT_RECYCLE_PERIOD = 60000;    //默认回收间隔

    /**
     * session映射
     */
    private final Map<SessionID, S> sessions = new HashMap<>(256);

    /**
     * 超时设置
     */
    private int timeout = DEFAULT_TIMEOUT;

    /**
     * 回收间隔
     */
    private int period = DEFAULT_RECYCLE_PERIOD;


    /**
     * 最大会话数目
     */
    private int maxSessions = 16 * 1024;

    /**
     * 如果会话数达到最大上限，要Trim多少时间之内没有访问的会话(Millisecond)
     */
    private int trimSpareTime = 60 * 1000;

    /**
     * 一次回收的数量
     */
    private int trimCount = 512;

    /**
     * 是否有线程在trim中
     */
    private boolean inTriming = false;

    /**
     * Session侦听者
     */
    private SessionListener listener;

    /**
     * 超时检测线程
     */
    private ScheduleService monitor;

    /**
     * session sessionDumper
     */
    private SessionDumper dumper = null;

    /**
     * 服务会话侦听者  ( Service Name --> SessionListener OR SessionListeners )
     */
    private Map<String, SessionListener> serviceSessionListeners
        = new HashMap<String, SessionListener>(8);

    private static Logger log = LoggerFactory.getLogger("apsis:session/container");

    /**
     * 返回会话的数目
     *
     * @return 数目
     */
    public int getCount() {
        return sessions.size();
    }

    /**
     * 返回会话有效期
     *
     * @return 有效期
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * 返回会话
     *
     * @param sessionId 会话标识
     * @return 会话，如果会话已经超时或者不存在返回<code>null</code>
     */
    public S getSession(SessionID sessionId) {
        S session = sessions.get(sessionId);
        if (session != null) {
            session.access(); //更新最后访问时间
        }
        return session;
    }

    /**
     * 获取所有的session，返回一个只读的集合
     *
     * @return sessions
     */
    @Property(writable = false)
    public Collection<S> getSessions() {
        return sessions.values();
    }

    /**
     * 导入一批session
     *
     * @param sessions
     */
    public void putAll(Collection<S> sessions) {
        if (sessions == null) {
            return;
        }
        for (S session : sessions) {
            //不重新激活Session Listener
            internalAdd(session);
        }
    }

    /**
     * 添加会话
     *
     * @param session 会话
     */
    public void addSession(S session) {
//        if (!(session instanceof ApsisSession)) {
//            throw new IllegalArgumentException("Invalid session instance:" + session.getClass());
//        }
        session.setTimeout(timeout);//重置超时时间
        session.access();//从现在开始计算超时
        if (listener != null) {
            listener.created(new SessionEvent(session));
        }
        internalAdd(session);
    }

    protected void internalAdd(S session) {
        if (session instanceof SessionContainerAware) {
            ((SessionContainerAware)session).setSessionContainer(this); //增加监听器，以便发布事件
        }
        if (sessions.size() > maxSessions) {
            trimSession(trimSpareTime, trimCount);
        }
        sessions.put(session.getSessionID(), session);
    }

    /**
     * 删除会话
     *
     * @param sessionId 会话标识
     */
    public void removeSession(SessionID sessionId) {
        S session = sessions.remove(sessionId);
        if (listener != null) {
            listener.destroyed(new SessionEvent(session));
        }
        if (session instanceof SessionContainerAware) {
            ((SessionContainerAware)session).setSessionContainer(null);
        }
        session.expire();
    }

    /**
     * 设置会话有效期
     *
     * @param timeout 有效期
     */
    public void setTimeout(int timeout) {
        if (timeout > 0) {
            this.timeout = timeout;
        }
    }

    /**
     * 添加Session侦听者
     *
     * @param sl Session侦听者
     */
    public synchronized void addSessionListener(SessionListener sl) {
        if (sl == null) {
            return;
        }
        if (this.listener == null) {
            this.listener = sl;
        }
        else if (this.listener instanceof SessionListeners) {
            ((SessionListeners)this.listener).addListener(sl);
        }
        else {
            SessionListeners listeners = new SessionListeners(this.listener);
            listeners.addListener(sl);
            this.listener = listeners;
        }
    }

    /**
     * 删除Session侦听者
     *
     * @param sl Session侦听者
     */
    public synchronized void removeSessionListener(SessionListener sl) {
        if (sl == null) {
            return;
        }
        if (this.listener == sl) {
            this.listener = null;
        }
        else if (this.listener instanceof SessionListeners) {
            SessionListeners listeners = ((SessionListeners)this.listener);
            listeners.removeListener(sl);
            if (listeners.getListenerCount() == 0) {
                this.listener = null;
            }
        }
    }

    /**
     * 返回Session侦听者
     *
     * @return SessionListener
     */
    public SessionListener getSessionListener() {
        return listener;
    }


    /**
     * 处理session超时
     */
    public void run() {
        int timeout = this.timeout;
        int size = sessions.size();
        int toTrim = 1024;
        if (size > 16 * 1024) {
            timeout = timeout / 8;
            toTrim = 512;
        }
        else if (size > 8 * 1024) {
            timeout = timeout / 4;
            toTrim = 256;
        }
        trimSession(timeout, toTrim);

    }

    /**
     * 初始化实现
     */
    public void _doInit() {
        if (dumper != null) {
            Runtime.getRuntime().addShutdownHook(new Thread(dumper, "dumper"));
            Collection<S> col = dumper.load();
            putAll(col);
        }
    }

    /**
     * 实际启动实现
     */
    public void _doStart() {
        monitor.scheduleAtFixedRate(this, period, period); //每五分钟检测一次
    }

    /**
     * 实际停止实现
     */
    public void _doStop() {
        monitor.stop();
    }

    /**
     * 实际销毁实现
     */
    public void _doDestroy() {
        monitor.destroy();
    }

    private final Object trimLock = new Object();

    /**
     * 快速回收会话，在压力极大时才会发生
     */
    private void trimSession(int time, int max) {
        //保证同时只有一个线程在Trim
        if (!inTriming) {
            synchronized (trimLock) {
                if (!inTriming) {
                    inTriming = true;
                    try {
                        trimSession0(time, max);
                    }
                    finally {
                        inTriming = false;
                    }
                }
            }
        }
    }

    private void trimSession0(int time, int max) {
        if (log.isTraceEnabled()) {
            log.trace("Try to recycle sessions,timeout=" + time + ";max=" + max);
        }
        long now = System.currentTimeMillis();
        long timestamp1 = now - time;
        long timestamp2 = now - trimSpareTime;
        List<S> toTrim = new ArrayList<>();
        synchronized (sessions) {
            boolean tooManySessions = sessions.size() > 8 * 1024;
            Iterator<S> values = sessions.values().iterator();
            long lastAccess;
            while (values.hasNext()) {
                S session = values.next();
                lastAccess = session.getLastAccessedTime();
                if (lastAccess < timestamp1 || (tooManySessions && lastAccess < timestamp2)) {
                    toTrim.add(session);
                    if (toTrim.size() > max) {
                        break;
                    }
                }
            }
        }
        try {

            for (int i = 0, size = toTrim.size(); i < size; i++) {
                S session = toTrim.get(i);
                SessionID sid = session.getSessionID();
                if (log.isDebugEnabled()) {
                    log.debug("Session timeout:" + sid);
                }
                removeSession(sid);
            }
        }
        catch (Throwable t) {
            log.warn("Remove session error", t);
        }
    }

    public SessionDumper getDumper() {
        return dumper;
    }

    public void setDumper(SessionDumper dumper) {
        if (dumper != null) {
            dumper.setContainer(this);
            this.dumper = dumper;
        }
    }

    public int getTrimCount() {
        return trimCount;
    }

    public void setTrimCount(int trimCount) {
        this.trimCount = trimCount;
    }

    public int getTrimSpareTime() {
        return trimSpareTime;
    }

    public void setTrimSpareTime(int trimSpareTime) {
        this.trimSpareTime = trimSpareTime;
    }

    public int getMaxSessions() {
        return maxSessions;
    }

    public void setMaxSessions(int maxSessions) {
        this.maxSessions = maxSessions;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
