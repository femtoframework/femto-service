package org.femtoframework.service.rmi;

import java.io.IOException;
import java.rmi.Remote;

/**
 * 远程资源装载器
 *
 * @author fengyun
 * @version 1.00 May 28, 2004 1:59:02 PM
 */
public interface RemoteLoader extends Remote
{
    /**
     * 根据类名返回类数据
     *
     * @param className 类名
     * @return 返回类数据
     * @throws IOException IOException
     * @throws SecurityException
     */
    byte[] getClassData(String className) throws IOException;

    /**
     * 根据类名返回类数据
     *
     * @param resourceName 资源名，采用"org/bolango/frame/xxx.properties"形式的名称
     * @return 返回类数据
     * @throws IOException       IOException
     * @throws SecurityException
     */
    byte[] getResourceData(String resourceName) throws IOException;
}
