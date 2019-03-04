package org.femtoframework.service.apsis.gmpp;

import org.femtoframework.net.gmpp.GmppConnection;
import org.femtoframework.parameters.Parameters;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 输出MagicNumber
 *
 * @author fengyun
 * @version 1.00 2005-6-3 1:07:15
 */
public class GmppConnectionEx extends GmppConnection
{
    /**
     * 初始化Socket
     *
     * @param socket
     * @param parameters 创建连接需要的参数
     * @return 是否关闭该连接
     */
    protected boolean initSocket(Socket socket, Parameters parameters)
    {
        try {
            OutputStream out = socket.getOutputStream();
            out.write(ApsisGmppConstants.GMPP_BIFURCATION);
            out.flush();

            InputStream in = socket.getInputStream();
            int r = in.read();
            return r != ApsisGmppConstants.GMPP_BIFURCATION;
        }
        catch (IOException e) {
            return true;
        }
    }
}
