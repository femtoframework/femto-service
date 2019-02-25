package org.femtoframework.cube.ext;

import org.femtoframework.io.IOUtil;

import java.net.Socket;

/**
 * 系统退出的Socket连接请求处理器<br>
 * 对于系统退出的请求，需要经过一个简单的认证过程：<br>
 * 客户端发送一个时间戳<br>
 * 服务器端读取时间戳，转化成字符串进行MD5后，将结果输出给客户端<br>
 * 客户端取得16字节后将其MD5之后送会给服务器，服务器确认合法后，<br>
 * 调用<code>System.exit</code>退出JVM<br>
 *
 * @author fengyun
 * @version 1.1 2005-3-10 12:34:51
 *          1.0 Feb 22, 2003 12:29:32 PM
 */
public class CubeStopServer extends CubeSocketHandler
{
    /**
     * 处理Socket
     *
     * @param socket Socket
     */
    public void handle(Socket socket)
    {
        //只有地址相同的连接才能停止该JVM
        boolean exit = verify(socket);
        IOUtil.close(socket);
        if (exit) {
            System.out.println("Trying to exit cube system......");
            System.exit(0);
        }
    }
}
