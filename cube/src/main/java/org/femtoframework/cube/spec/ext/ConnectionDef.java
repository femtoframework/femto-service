package org.femtoframework.cube.spec.ext;

import org.femtoframework.bean.Initializable;
import org.femtoframework.bean.exception.InitializeException;
import org.femtoframework.cube.spec.ConnectionSpec;
import org.femtoframework.parameters.ParametersMap;
import org.femtoframework.util.StringUtil;

import static org.femtoframework.cube.CubeConstants.COUNT;
import static org.femtoframework.cube.CubeConstants.HOST;
import static org.femtoframework.cube.CubeConstants.BACKEND;

/**
 * 连接定义
 *
 * @author fengyun
 * @version 1.00 2004-9-5 0:19:19
 */
public class ConnectionDef extends ParametersMap
    implements ConnectionSpec, Initializable
{
    /**
     * 返回目标主机模式
     *
     * @return 目标主机模式
     */
    public String getHost()
    {
        return getString(HOST, "*");
    }

    /**
     * 返回目标服务器模式
     *
     * @return 目标服务器模式
     */
    public String getBackend()
    {
        return getString(BACKEND);
    }

    /**
     * 返回连接数目
     *
     * @return 连接数目
     */
    public int getCount()
    {
        return getInt(COUNT, 1);
    }

    /**
     * 设置主机配置
     *
     * @param host 主机配置
     */
    public void setHost(String host)
    {
        put(HOST, host);
    }

    /**
     * 返回服务器配置
     *
     * @param server 服务器配置
     */
    public void setServer(String server)
    {
        put(BACKEND, server);
    }

    /**
     * 设置连接数目.
     *
     * @param count 连接数目
     */
    public void setCount(int count)
    {
        put(COUNT, count);
    }

    /**
     * 初始化
     */
    public void initialize()
    {
        if (StringUtil.isInvalid(getBackend())) {
            throw new InitializeException("No 'backend' attribute of a <connection>");
        }
    }
}
