package org.femtoframework.service.apsis.balance;

import org.bolango.apsis.ApsisSession;
import org.bolango.apsis.session.ext.SimpleSessionGenerator;
import org.bolango.frame.SessionID;

/**
 * 负载均衡会话产生器
 *
 * @author fengyun
 * @version 1.00 2005-11-17 10:34:04
 */
public class BalanceSessionGenerator extends SimpleSessionGenerator
{
    /**
     * 构造负载均衡产生器
     */
    public BalanceSessionGenerator()
    {
        setServerID(BalanceServerID.getLocal());
    }

    /**
     * 根据会话标识产生会话
     *
     * @param sid 会话标识
     * @return Session 会话
     */
    protected ApsisSession generate(SessionID sid)
    {
        return new ApsisBalanceSession(sid);
    }
}
