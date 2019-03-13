package org.femtoframework.service.apsis.session;

import org.femtoframework.pattern.EventListeners;
import org.femtoframework.service.SessionEvent;
import org.femtoframework.service.SessionListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * SessionListener集合
 *
 * @author fengyun
 * @version 1.00 Dec 30, 2003 7:28:43 PM
 */
public class SessionListeners
    implements SessionListener
{

    private EventListeners<SessionListener> listeners = null;

    public SessionListeners()
    {
        listeners = new EventListeners<>();
    }

    public SessionListeners(SessionListener listener)
    {
        listeners = new EventListeners<>(listener);
    }

    /**
     * 添加事件侦听器
     *
     * @param listener 侦听者
     */
    public void addListener(SessionListener listener)
    {
        listeners.addListener(listener);
    }

    /**
     * 删除事件侦听者
     *
     * @param listener 侦听者
     */
    public void removeListener(SessionListener listener)
    {
        listeners.removeListener(listener);
    }

    /**
     * 返回侦听者数组
     *
     * @return 侦听者数组
     */
    private List<SessionListener> getListenerList()
    {
        return listeners.getListeners();
    }

    /**
     * 返回侦听者总数
     *
     * @return 侦听者总数
     */
    public int getListenerCount()
    {
        return listeners.getListenerCount();
    }

    /**
     * 串行化
     *
     * @param oos 对象输出流
     * @throws IOException 当发生I/O异常时
     */
    public void marshal(ObjectOutputStream oos)
        throws IOException
    {
        oos.writeObject( listeners);
    }

    /**
     * 反串行化
     *
     * @param ois 对象输入流
     * @throws IOException    当发生I/O异常时
     * @throws ClassNotFoundException
     */
    public void demarshal(ObjectInputStream ois)
        throws IOException, ClassNotFoundException
    {
        listeners = (EventListeners<SessionListener>) ois.readObject();
    }

    /**
     * Notification that a session was created.
     *
     * @param se the notification event
     */
    public void created(SessionEvent se)
    {
        List<SessionListener> list = getListenerList();
        for (SessionListener sessionListener : list) {
            sessionListener.created(se);
        }
    }

    /**
     * Notification that a session was invalidated.
     *
     * @param se the notification event
     */
    public void destroyed(SessionEvent se)
    {
        List<SessionListener> list = getListenerList();
        for (SessionListener sessionListener : list) {
            sessionListener.destroyed(se);
        }
    }
}

