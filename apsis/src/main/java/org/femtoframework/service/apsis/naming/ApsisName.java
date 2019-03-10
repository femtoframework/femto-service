package org.femtoframework.service.apsis.naming;


/**
 * ApsisName
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2006-3-6 16:22:01
 */
public class ApsisName extends Path implements Cloneable
{
    private static final char SEP = '/';

    /**
     * 根据字符串构造
     *
     * @param path 路径
     * @throws NullPointerException 如果<code>path == null</code>
     */
    public ApsisName(String path)
    {
        super(path, SEP);
    }


    /**
     * 根据数组和分隔符构造
     *
     * @param groups 数组
     * @param path   最初的路径
     */
    public ApsisName(String[] groups, String path)
    {
        super(groups, SEP, path);
    }

    /**
     * 根据数组和分隔符构造
     *
     * @param groups       数组
     * @param off          起始级别
     * @param size         级数
     * @param path         最初的路径
     * @param hasPrefixSep 是否拥有分隔符前缀
     * @param hasSuffixSep 是否拥有分隔符后缀
     */
    public ApsisName(String[] groups, int off, int size, String path, boolean hasPrefixSep, boolean hasSuffixSep)
    {
        super(groups, off, size, SEP, path, hasPrefixSep, hasSuffixSep);
    }

    /**
     * <pre>
     * 通配符说明：
     *  +              任选一个服务器
     *  *              所有服务器
     *  $              本地服务器
     *  %              所有远程服务器
     *  ^              所有服务器，但是只选择第一个有效成功的结果返回，
     *                 适用场景是设备随机连接了一个服务器，而且可能在不同服务器之间快速切换，
     *                 所以我们很难定位具体这个设备连接了哪个服务器，所以采用广播的形式发送下发的报文，
     *                 有成功返回的结果就直接采用第一个结果。
     *
     *  @              优先选择本地的服务器
     *  #serverType    选择指定类型的服务器
     *  =balanceType  指定采用什么样的选取器（random round_robin hash)
     *  !host:port             表示向指定的主机和端口的服务器发送
     * </pre>
     */
    public static boolean isSpecialPrefix(String server)
    {
        if (server.length() >= 1) {
            char first = server.charAt(0);
            return first == '+' || first == '*' || first == '$' || first == '%' || first == '@'
                   || first == '#' || first == '=' || first == '!' | first == '^';
        }
        return false;
    }
}
