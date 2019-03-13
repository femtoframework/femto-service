package org.femtoframework.service.apsis.session;

import org.femtoframework.parameters.Parameters;
import org.femtoframework.service.ServerID;
import org.femtoframework.service.Session;
import org.femtoframework.service.SessionID;
import org.femtoframework.service.apsis.SimpleSessionID;
import org.femtoframework.util.DataUtil;
import org.femtoframework.util.crypto.MD5;

/**
 * ApsisSessionGenerator
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-7-26 16:39:38
 */
public class ApsisSessionGenerator<S extends Session>
    implements SessionGenerator<S>
{
    public static final char SEP = ':';
    /**
     * 服务标识
     */
    protected ServerID serverID;

    /**
     * 构造会话发生器
     */
    public ApsisSessionGenerator()
    {
    }


    /**
     * 设置服务标识
     *
     * @param serverId 服务标识
     */
    public void setServerID(ServerID serverId)
    {
        this.serverID = serverId;
    }

    /**
     * 根据环境变量产生会话
     *
     * @param env 环境变量
     */
    public S generate(Parameters env)
    {
        return generate(null, env);
    }

    /**
     * 根据会话标识和环境变量产生会话
     *
     * @param sessionId 会话标识，如果为<code>null<code>则调用<br>
     *                  #generateID(Parameters)产生标识<br>
     * @param env       环境变量
     */
    public S generate(SessionID sessionId, Parameters env)
    {
        if (sessionId == null || !isValid(sessionId)) {
            //产生会话标识
            sessionId = generateID(env);
        }

        //产生会话
        S session = generate(sessionId);
        //初始化会话
        initSession(session, env);

        return session;
    }


    /**
     * 判断会话标识是否有效
     *
     * @param sessionId 会话标识
     * @return 是否有效
     */
    public boolean isValid(String sessionId)
    {
        SimpleSessionID sid = new SimpleSessionID(sessionId);
        try {
            return isValid(sid);
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断会话标识是否有效
     *
     * @param sessionId 会话标识
     * @return 是否有效
     */
    public boolean isValid(SessionID sessionId)
    {
        if (!(sessionId instanceof SimpleSessionID)) {
            return false;
        }
        SimpleSessionID ssId = (SimpleSessionID) sessionId;
        long serverID = ssId.getServerId();
        int id = ssId.getIdentity();
        String key = generateKey(serverID, id);
        byte[] b1 = MD5.encrypt(key);
        if (b1 == null) {
            return false;
        }

        String validation = String.valueOf(ssId.getValidation());
        if (validation.length() != 8) {
            return false;
        }

        int v1, v2;
        v1 = ((b1[0] & 0x40) | (b1[1] & 0x30) | (b1[2] & 0x0C) | (b1[3] & 0x03)) % 10;
        v2 = validation.charAt(1) - '0';
        if (v1 != v2) {
            return false;
        }
        v1 = ((b1[4] & 0x40) | (b1[5] & 0x30) | (b1[6] & 0x0C) | (b1[7] & 0x03)) % 10;
        v2 = validation.charAt(3) - '0';
        if (v1 != v2) {
            return false;
        }
        v1 = ((b1[8] & 0x40) | (b1[9] & 0x30) | (b1[10] & 0x0C) | (b1[11] & 0x03)) % 10;
        v2 = validation.charAt(5) - '0';
        if (v1 != v2) {
            return false;
        }
        v1 = ((b1[12] & 0x40) | (b1[13] & 0x30) | (b1[14] & 0x0C) | (b1[15] & 0x03)) % 10;
        v2 = validation.charAt(7) - '0';
        if (v1 != v2) {
            return false;
        }

        return true;
    }

    /**
     * 产生唯一标识
     *
     * @param env 环境
     * @return 返回唯一标识串
     */
    protected String generateIdentity(Parameters env)
    {
        //产生标识串
        StringBuilder sb = new StringBuilder();
        //用户名
        String user = getString(env, "username");
        if (user != null) {
            sb.append(user);
            //域名
            append(sb, env, "domainname");
        }
        else { //Email
            append(sb, env, "email");
        }
        //客户端IP
        append(sb, env, "client");
        //添加时间标签
        sb.append(System.currentTimeMillis());
        //返回
        return sb.toString();
    }

    private static String getString(Parameters env, String key)
    {
        return env != null ? env.getString(key) : null;
    }

    /**
     * 产生校验因子，根据服务器和会话唯一标识校验
     *
     * @param serverId 服务标识
     * @param id       会话唯一标识
     */
    protected String generateKey(long serverId, int id)
    {
        //产生标识串
        StringBuilder sb = new StringBuilder(32);
        sb.append(serverId);
        sb.append(SEP);
        sb.append(id);
        return sb.toString();
    }

    /**
     * 添加一个环境变量的值
     *
     * @param sb  StringBuilder
     * @param env 环境变量
     * @param key 键
     */
    private static StringBuilder append(StringBuilder sb,
                                              Parameters env,
                                              String key)
    {
        String value = getString(env, key);
        if (value != null) {
            sb.append(value);
        }
        return sb;
    }

    /**
     * 根据会话标识产生会话
     *
     * @param sid 会话标识
     * @return Session 会话
     */
    protected S generate(SessionID sid)
    {
        return (S)new ApsisSession(sid);
    }

    /**
     * 回收会话
     */
    public void recycle(S session)
    {
        if (session != null) {
            session.expire();
        }
    }

    /**
     * 初始化变量
     *
     * @param session 会话
     * @param env     参数
     */
    protected void initSession(S session, Parameters env)
    {
        //扩展
    }

    /**
     * 产生会话标识
     *
     * @param env 环境变量
     */
    public SessionID generateID(Parameters env)
    {
        //产生唯一标识串String
        String identity = generateIdentity(env);
        return generate0(serverID.getId(), identity);
    }

//    /**
//     * 根据服务标识产生会话标识
//     *
//     * @param serverID
//     * @return
//     */
//    public SessionID generateID(ServerID serverID)
//    {
//        String identity = generateIdentity(null);
//        return generate0(serverID, identity);
//    }

    //SessionID
    /**
     * 下一个会话号<br>
     * 从10开始，前面的用于特殊用途，有效长度是5位以内，循环使用<br>
     */
    private static int nextSessionId = 256;

    /**
     * 返回下一个服务标识
     *
     * @return 服务标识
     */
    static synchronized int getNextSessionID()
    {
        int sid = nextSessionId++ % 1000000;
        if (sid < 256) {
            nextSessionId = 257;
            sid = 256;
        }
        return sid;
    }

    private SessionID generate0(long serverID, String identity)
    {
        //产生校验因子
        int id = getNextSessionID();
        String key = generateKey(serverID, id);

        byte[] bytes1 = MD5.encrypt(identity);
        byte[] bytes2 = MD5.encrypt(key);
        String validation = merge(bytes1, bytes2);

        //产生服务器标识
        return new SimpleSessionID(serverID, id, Integer.parseInt(validation));
    }

    /**
     * 压缩
     */
    protected static final String merge(byte[] b1, byte[] b2)
    {
        if (b1 == null || b2 == null ||
            b1.length != b2.length) {
            return DataUtil.EMPTY_STRING;
        }

        StringBuilder sb = new StringBuilder(8);
        int value = 0;
        value = (b1[0] & 0x40) | (b1[1] & 0x30) | (b1[2] & 0x0C) | (b1[3] & 0x03);
        sb.append(NUMBER[value % 10]);
        value = (b2[0] & 0x40) | (b2[1] & 0x30) | (b2[2] & 0x0C) | (b2[3] & 0x03);
        sb.append(NUMBER[value % 10]);
        value = (b1[4] & 0x40) | (b1[5] & 0x30) | (b1[6] & 0x0C) | (b1[7] & 0x03);
        sb.append(NUMBER[value % 10]);
        value = (b2[4] & 0x40) | (b2[5] & 0x30) | (b2[6] & 0x0C) | (b2[7] & 0x03);
        sb.append(NUMBER[value % 10]);
        value = (b1[8] & 0x40) | (b1[9] & 0x30) | (b1[10] & 0x0C) | (b1[11] & 0x03);
        sb.append(NUMBER[value % 10]);
        value = (b2[8] & 0x40) | (b2[9] & 0x30) | (b2[10] & 0x0C) | (b2[11] & 0x03);
        sb.append(NUMBER[value % 10]);
        value = (b1[12] & 0x40) | (b1[13] & 0x30) | (b1[14] & 0x0C) | (b1[15] & 0x03);
        sb.append(NUMBER[value % 10]);
        value = (b2[12] & 0x40) | (b2[13] & 0x30) | (b2[14] & 0x0C) | (b2[15] & 0x03);
        sb.append(NUMBER[value % 10]);
        return sb.toString();
    }

    private static final char[] NUMBER
        = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
}