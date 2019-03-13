package org.femtoframework.service.apsis.balance;

import org.femtoframework.service.SessionID;
import org.femtoframework.service.apsis.session.ApsisSession;
import org.femtoframework.service.apsis.session.ApsisSessionGenerator;

/**
 * 负载均衡会话产生器
 *
 * @author fengyun
 * @version 1.00 2005-11-17 10:34:04
 */
public class BalanceSessionGenerator extends ApsisSessionGenerator<ApsisBalanceSession>
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
    protected ApsisBalanceSession generate(SessionID sid)
    {
        return new ApsisBalanceSession(sid);
    }
}
