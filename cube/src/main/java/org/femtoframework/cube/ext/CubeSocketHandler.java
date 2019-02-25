package org.femtoframework.cube.ext;

import org.femtoframework.io.CodecUtil;
import org.femtoframework.io.IOUtil;
import org.femtoframework.net.socket.bifurcation.BifurcatedSocketHandler;
import org.femtoframework.util.ArrayUtil;
import org.femtoframework.util.crypto.MD5;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Cube Socket处理器（内部类）
 *
 * @author fengyun
 * @version 1.00 2005-3-10 12:38:02
 */
abstract class CubeSocketHandler extends BifurcatedSocketHandler
{

    protected boolean verify(Socket socket)
    {
        try {
            InputStream input = socket.getInputStream();

            //读取时间戳
            long time = CodecUtil.readLong(input);

            //MD5
            byte[] md5 = MD5.encrypt(String.valueOf(time));

            //输出
            OutputStream output = socket.getOutputStream();
            output.write(md5);
            output.flush();

            byte[] encrypted = new byte[16];
            int read = IOUtil.readFully(input, encrypted);

            if (read != 16) {
                return false;
            }

            byte[] verify = MD5.encrypt(md5);

            return ArrayUtil.matches(verify, 0, encrypted, 0, 16);
        }
        catch (Throwable t) {
            return false;
        }
    }
}
