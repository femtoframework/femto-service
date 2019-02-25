package org.femtoframework.cube.ext;

import org.femtoframework.net.socket.bifurcation.BifurcatedSocketHandler;
import org.femtoframework.net.socket.bifurcation.BifurcationSocketHandlerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * CubeSocketHandlerFactory
 *
 * @author fengyun
 * @version 1.00 2005-3-14 20:28:19
 */
public class CubeSocketHandlerFactory
    implements BifurcationSocketHandlerFactory
{

    private final Map<Integer, BifurcatedSocketHandler> map = new HashMap<>();

    /**
     * 根据bifurcation返回相应的处理器
     *
     * @param bifurcation Bifurcation
     * @return SocketHandler
     */
    public BifurcatedSocketHandler getHandler(int bifurcation)
    {
        return map.get(bifurcation);
    }

    /**
     * 添加处理器
     *
     * @param handler BifurcatedSocketHandler
     */
    public void addHandler(BifurcatedSocketHandler handler)
    {
        map.put(handler.getBifurcation(), handler);
    }
}
