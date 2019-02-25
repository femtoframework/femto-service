package org.femtoframework.cube.ext;

import org.femtoframework.io.IOUtil;
import org.femtoframework.net.socket.SocketHandler;
import org.femtoframework.net.socket.endpoint.CheckLaunched;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

/**
 * 检查Socket是否已经处理的SocketHandler
 *
 * @author fengyun
 * @version 1.00 2005-3-10 10:21:20
 */
public class CheckLaunchedHandler
    implements CheckLaunched
{
    /**
     * 是否检查已经被Launched
     */
    private boolean checkLaunched = true;

    /**
     * 日志
     */
    private static Logger log = LoggerFactory.getLogger("net/socket/check_launched");


    private SocketHandler handler;

    /**
     * 处理Socket
     *
     * @param socket Socket
     */
    public void handle(Socket socket)
    {
        if (checkLaunched) {
            if (doCheckLaunched()) {
                checkLaunched = false;
            }
            else { //Close the socket
                log.warn("JVM still in launch, closing the socket:" + socket);
                IOUtil.close(socket);
                return;
            }
        }
        handler.handle(socket);
    }

    private boolean doCheckLaunched() {
        return Boolean.TRUE == System.getProperties().get("cube.system.launched");
    }

    /**
     * 是否检查已经启动了
     *
     * @return 是否检查已经启动了
     */
    public boolean isCheckLaunched()
    {
        return checkLaunched;
    }

    /**
     * 设置是否检查已经启动
     *
     * @param checkLaunched
     */
    public void setCheckLaunched(boolean checkLaunched)
    {
        this.checkLaunched = checkLaunched;
    }

    public SocketHandler getHandler() {
        return handler;
    }

    public void setHandler(SocketHandler handler) {
        this.handler = handler;
    }
}
