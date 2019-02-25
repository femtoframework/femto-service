package org.femtoframework.cube.ext;

import org.femtoframework.io.IOUtil;

import java.net.Socket;

/**
 * 维持状态的服务器
 *
 * @author fengyun
 * @version 1.00 2005-3-10 12:33:18
 */
public class CubeStatusServer extends CubeSocketHandler
{
    /**
     * 处理Socket
     *
     * @param socket Socket
     */
    public void handle(Socket socket)
    {
        try {
            verify(socket);
        }
        finally {
            IOUtil.close(socket);
        }
    }
}
