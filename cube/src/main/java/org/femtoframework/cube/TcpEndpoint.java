package org.femtoframework.cube;

/**
 * Tcp Endpoint接口
 *
 * @author fengyun
 * @version 1.00 2005-5-11 9:32:47
 */
public interface TcpEndpoint extends Endpoint
{
    /**
     * 返回主机地址
     */
    String getHost();
}
