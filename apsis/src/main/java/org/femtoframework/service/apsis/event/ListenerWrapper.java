package org.femtoframework.service.apsis.event;

import org.femtoframework.bean.annotation.Action;
import org.femtoframework.service.Event;
import org.femtoframework.service.EventValve;
import org.femtoframework.service.NoSuchActionException;
import org.femtoframework.service.event.EventPipeline;
import org.femtoframework.service.event.GenericEvent;
import org.femtoframework.text.NamingConvention;
import org.femtoframework.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 桢听者封装
 *
 * @author fengyun
 * @version 1.00 2005-9-19 22:44:59
 */
public class ListenerWrapper extends EventContainerBase
    implements EventPipeline
{
    private static final String PREFIX = "on";
    private static final String DEFAULT_ACTION = "event";

    private Object listener = null;

    /**
     * Action --> Method
     */
    private HashMap<String, Method> actions = new HashMap<String, Method>(8);

    public ListenerWrapper(Object listener)
    {
        this.listener = listener;
    }

    public final Method getMethod(String action)
    {
        return actions.get(action);
    }

    /**
     * 判断是否拥有该方法
     *
     * @param action 方法名
     * @return 是否拥有该方法
     */
    public final boolean hasAction(String action)
    {
        return actions.containsKey(action);
    }

    /**
     * 返回所有的Action名
     *
     * @return 所有的Action名
     */
    public Collection<String> getActionNames()
    {
        return actions.keySet();
    }

    /**
     * 返回所有的方法
     */
    public final Collection<Method> getActionMethods()
    {
        return actions.values();
    }

    /**
     * 执行方法
     *
     * @param method 方法
     * @param param  参数
     * @throws Exception
     */
    protected void invokeMethod(Method method, Object... param)
        throws Exception
    {
        try {
            method.invoke(listener, param);
        }
        catch (IllegalAccessException iae) {
            throw iae;
        }
        catch (InvocationTargetException ite) {
            Throwable t = ite.getTargetException();
            if (t instanceof Exception) {
                throw(Exception)t;
            }
            else {
                throw(Error)t;
            }
        }
    }

    public void _doInit()
    {
        super._doInit();

        initActions();
    }

    /**
     * 处理事件
     *
     * @param event Event
     */
    public void doHandle(Object event) throws Exception
    {
        if (event instanceof Event) {
            doHandle((Event)event);
        }
        else {
            doHandle((GenericEvent)event);
        }
    }

    /**
     * 返回侦听者的对应类
     *
     * @return 侦听者的对应类
     */
    protected Class getListenerClass()
    {
        return listener.getClass();
    }

    /**
     * 判断对象是否是Listener
     *
     * @param obj 对象
     * @return 对象是否是Listener
     */
    static boolean isListener(Object obj)
    {
        if (obj instanceof EventListener) {
            return true;
        }
        Class clazz = obj.getClass();
        List<Method> methods = getMethods(clazz);
        return !methods.isEmpty();
    }

    public static List<Method> getMethods(Class clazz)
    {
        Method[] methods = clazz.getMethods();
        List<Method> list = new ArrayList<Method>();
        for (Method method : methods) {
            int modifier = method.getModifiers();
            boolean isAccepted = Modifier.isPublic(modifier) && !Modifier.isStatic(modifier);
            if (isAccepted) {
                String methodName = method.getName();
                if (methodName.startsWith(PREFIX)) {
                    list.add(method);
                } else if (method.isAnnotationPresent(Action.class)) {
                    list.add(method);
                }
            }
        }
        return list;
    }

    static ListenerWrapper createWrapper(String name, Object listener)
    {
        ListenerWrapper wrapper = new ListenerWrapper(listener);
        wrapper.setName(name);
        wrapper.init();
        wrapper.start();
        return wrapper;
    }

    protected void initActions()
    {
        //采用BeanUtil来出始化
        Class clazz = getListenerClass();
        List<Method> methods = getMethods(clazz);
        for (Method method : methods) {
            String methodName = method.getName();
            String action = methodName;
            if (methodName.startsWith(PREFIX)) {
                action = NamingConvention.toPropertyName(methodName, PREFIX);
            }
            actions.put(action, method);
            actions.put(NamingConvention.format(action), method);
        }
    }

    /**
     * 处理事件
     *
     * @param event 事件
     */
    public void doHandle(Event event) throws Exception
    {
        String action = event.getAction();
        if (StringUtil.isInvalid(action)) {
            action = DEFAULT_ACTION;
        }
        Object[] param = new Object[]{event};
        Method method = getMethod(action);
        if (method == null) {
            throw new NoSuchActionException(getName(), action);
        }

        //检查会话
//        checkSession(method, event);
        invokeMethod(method, param);
    }

//    private void checkSession(Method method, Event event)
//        throws ExecutionException
//    {
//        SessionModel model = null;
//        if (method.isAnnotationPresent(SessionModel.class)) {
//            model = method.getAnnotation(SessionModel.class);
//        }
//        else if (method.getDeclaringClass().isAnnotationPresent(SessionModel.class)) {
//            model = method.getDeclaringClass().getAnnotation(SessionModel.class);
//        }
//        if (model != null) {
//            event.getSession(model.scope(), false, true);
//        }
//    }

    public void doHandle(GenericEvent event) throws Exception
    {
        String action = event.getAction();
        if (StringUtil.isInvalid(action)) {
            action = DEFAULT_ACTION;
        }
//        Object[] param = new Object[]{event.getEventArgs()};
        Method method = getMethod(action);
        if (method == null) {
            throw new NoSuchActionException(getName(), action);
        }
        Object[] args = event.getEventArgs();
        if (args != null) {
            invokeMethod(method, args);
        }
        else {
            invokeMethod(method);
        }
    }

    /**
     * 返回原始的侦听者
     *
     * @return 原始的侦听者
     */
    public Object getListener()
    {
        return listener;
    }

    /**
     * 返回基础阀门
     *
     * @return 基础阀门
     */
    public EventValve getBasic()
    {
        return pipeline.getBasic();
    }

    /**
     * 设置基础阀门
     *
     * @param valve 基础阀门
     */
    public void setBasic(EventValve valve)
    {
        pipeline.setBasic(valve);
    }
}
