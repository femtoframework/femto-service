package org.femtoframework.cube.ext;

import org.femtoframework.cube.CubeConstants;
import org.femtoframework.io.CodecUtil;
import org.femtoframework.io.IOUtil;
import org.femtoframework.util.crypto.MD5;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Cube控制器（查询应用服务器状态和停止应用服务器）
 *
 * @author fengyun
 * @version 1.00 2005-3-10 12:42:27
 */
public class CubeController
{
    public static void stop(String host, int port)
        throws Exception
    {
        action("stop", host, port, CubeConstants.BIFURCATION_STOP);
    }

    public static void status(String host, int port)
        throws Exception
    {
        action("status", host, port, CubeConstants.BIFURCATION_STATUS);
    }

    public static void action(String action, String host, int port)
        throws Exception
    {
        if ("stop".equals(action)) {
            stop(host, port);
        }
        else {
            status(host, port);
        }
    }

    private static void action(String action, String host, int port, int magic)
        throws Exception
    {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 10000);
        socket.setSoTimeout(100000);
        OutputStream out = socket.getOutputStream();
        out.write(magic);
        out.flush();

        InputStream in = socket.getInputStream();
        int m = in.read();
        if (m != magic) {
            System.out.println("Can't " + action + " the jvm");
            System.exit(0);
        }

        CodecUtil.writeLong(out, System.currentTimeMillis());
        byte[] bytes = new byte[16];
        int len = IOUtil.readFully(in, bytes);
        if (len != 16) {
            System.out.println("Can't " + action + " the jvm");
            System.exit(0);
        }

        byte[] md5 = MD5.encrypt(bytes);
        out.write(md5);
        out.flush();
    }

    public static void main(String[] args)
        throws Exception
    {
        if (args.length != 3) {
            System.out.println("Usage: java org.femtoframework.cube.ext.CubeController stop|status host port");
            System.exit(0);
        }
        String action = args[0];
        if (!("status".equals(action) || "stop".equals(action))) {
            System.out.println("Usage: java org.femtoframework.cube.ext.CubeController stop|status host port");
            System.exit(0);
        }
        String host = args[1];
        int port = Integer.parseInt(args[2]);
        action(action, host, port);
    }
}
