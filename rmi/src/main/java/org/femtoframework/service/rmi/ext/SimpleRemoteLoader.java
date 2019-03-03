package org.femtoframework.service.rmi.ext;

import org.femtoframework.io.ByteArrayOutputStream;
import org.femtoframework.io.IOUtil;
import org.femtoframework.service.rmi.RemoteLoader;
import org.femtoframework.service.rmi.server.RemoteObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * 远程资源装载器实现
 *
 * @author fengyun
 * @version 1.00 2005-5-22 20:50:00
 */
public class SimpleRemoteLoader extends RemoteObject
    implements RemoteLoader
{
    private ClassLoader loader;

    /**
     * 构造
     *
     * @param loader 类装载器
     */
    public SimpleRemoteLoader(ClassLoader loader)
    {
        this.loader = loader;
    }

    /**
     * 根据类名返回类数据
     *
     * @param className 类名
     * @return 返回类数据
     * @throws IOException IOException
     * @throws SecurityException
     */
    public byte[] getClassData(String className) throws IOException
    {
        try {
            loader.loadClass(className);
        }
        catch (Exception e) {
            throw new IOException("Loading class exception:" + e.getMessage());
        }
        String resourceName = className.replace('.', '/') + ".class";
        return getResourceData(resourceName);
    }

    /**
     * 根据类名返回类数据
     *
     * @param resourceName 资源名，采用"org/bolango/frame/xxx.properties"形式的名称
     * @return 返回类数据
     * @throws IOException IOException
     * @throws SecurityException
     */
    public byte[] getResourceData(String resourceName) throws IOException
    {
        InputStream input = loader.getResourceAsStream(resourceName);
        if (input == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtil.copy(input, baos);
        byte[] bytes = baos.toByteArray();
        IOUtil.close(input);
        IOUtil.close(baos);
        return bytes;
    }
}
