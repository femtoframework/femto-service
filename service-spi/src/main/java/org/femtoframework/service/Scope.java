package org.femtoframework.service;


import org.femtoframework.bean.NamedBean;

/**
 * Scope枚举
 *
 * @author fengyun
 * @version 1.00 2005-4-7 11:52:55
 */
public enum Scope implements NamedBean
{
    REQUEST("request"), NAMESPACE("namespace"), SERVER("server");

    /**
     * 变量属于请求级范围
     */
    public static final int SCOPE_REQUEST = 0;

    /**
     * 变量属于名字空间的会话级范围
     */
    public static final int SCOPE_NAMESPACE = 1;

    /**
     * 变量属于服务器的会话级范围
     */
    public static final int SCOPE_SERVER = 2;

    private String name;

    private static final Scope[] VALUES = {REQUEST, NAMESPACE, SERVER};

    /**
     * 构造
     *
     * @param name 名称
     */
    Scope(String name)
    {
        this.name = name;
    }

    /**
     * 返回名称
     *
     * @return 名称
     */
    public String getName()
    {
        return name;
    }

    public static Scope getScope(int scope)
    {
        if (scope < 0 || scope > 2) {
            return null;
        }
        return VALUES[scope];
    }
}
