package org.femtoframework.service.apsis.session;

import org.femtoframework.implement.ImplementUtil;
import org.femtoframework.service.*;
import org.femtoframework.service.apsis.ApsisConstants;

import javax.security.auth.Subject;

/**
 * ApsisSessionContext
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-7-25 15:32:27
 */
public class ApsisSessionContext implements SessionContext
{
    private SessionContainer<ApsisSession> container = null;
    private Arguments arguments;
    private ApsisSession session = null;
    private SessionGenerator<ApsisSession> generator = ImplementUtil.getInstance(SessionGenerator.class);

    {
        generator.setServerID(ServerID.getLocal());
    }

    /**
     * 构造
     *
     * @param container
     * @param args
     */
    public ApsisSessionContext(SessionContainer<ApsisSession> container, Arguments args)
    {
        this.container = container;
        this.arguments = args;
    }

    public SessionContainer getContainer()
    {
        return container;
    }

    /**
     * 创建当前用户在当前服务中的Session，它与getSession(true)不同的是它不把创建的会话保存为自身使用的会话
     *
     * @return [Session] 创建的会话
     */
    public Session createSession()
    {
        return generator.generate(arguments);
    }

    /**
     * 返回当前用户在当前服务中的Session
     *
     * @return [Session] 当前用户会话
     */
    public Session getSession()
    {
        return getSession(false);
    }

    /**
     * 返回当前用户在当前服务中的Session
     *
     * @param create 如果<code>true</code>，当<code>session==null</code>时建会话
     * @return [Session] 当前用户会话
     */
    public Session getSession(boolean create)
    {
        if (session == null) {
            if (arguments.getSessionID() != null) {
                session = container.getSession(arguments.getSessionID());
            }
            if (session == null && create) {
                session = generator.generate(arguments);
                //Copy 默认的字段
                session.put(Constants.ATTR_LOCALE, arguments.getLocale());
                session.put(Constants.ATTR_CLIENT_IP_ADDRESS,
                        arguments.getString(Constants.ATTR_CLIENT_IP_ADDRESS, ""));

                arguments.setSessionID(session.getSessionID());

                //自动设置Subject
                Subject subject = (Subject) arguments.get(ApsisConstants.SUBJECT);
                if (subject != null) {
                    session.setSubject(subject);
                    //自动设置用户
                    Object user = arguments.get(ApsisConstants.USER);
                    if (user != null) {
                        session.put("user", user);
                    }
                }


                container.addSession(session);
            }
        }
        return session;
    }

    /**
     * 结束当前用户在当前服务中的Session
     */
    public void endSession()
    {
        SessionID sid = arguments.getSessionID();
        if (session == null && sid != null) {
            session = container.getSession(sid);
        }
        if (session != null) {
            container.removeSession(session.getSessionID());
        }
    }
}