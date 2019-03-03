package org.femtoframework.service.rmi;

import org.femtoframework.implement.ImplementUtil;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;

/**
 * RMI相关的工具类
 *
 * @author fengyun
 * @version 1.00 2005-5-22 20:55:12
 */
public class RmiUtil {
    private RmiUtil() {
    }

    private static RmiModule module = ImplementUtil.getInstance(RmiModule.class);

    /**
     * 对象是否已经导出
     *
     * @param obj 对象
     * @return 是否已经导出
     */
    public static boolean isExported(Object obj) {
        return module.isExported(obj);
    }

//    /**
//     * 根据给定的类转载器返回响应的远程装载器
//     *
//     * @param loader 类装载器
//     * @return 返回远程装载器
//     */
//    public static RemoteLoader getRemoteLoader(ClassLoader loader) {
//        return module.getRemoteLoader(loader);
//    }

    /**
     * Export the remote object to make it available to receive incoming calls,
     * using an anonymous port.
     * 使用StrOID方式导出对象
     *
     * @param obj the remote object to be exported
     * @param uri 导出的名称
     * @return remote object stub
     * @throws RemoteException
     *          if export fails
     * @throws ExportException
     *          if export fails
     * @see StrOID
     */
    public static Remote exportObject(Object obj, String uri) {
        return module.exportObject(obj, uri);
    }

    /**
     * Remove the remote object, obj, from the RMI runtime. If
     * successful, the object can no longer accept incoming RMI calls.
     * If the force parameter is true, the object is forcibly unexported
     * even if there are pending calls to the remote object or the
     * remote object still has calls in progress.  If the force
     * parameter is false, the object is only unexported if there are
     * no pending or in progress calls to the object.
     *
     * @param obj   the remote object to be unexported
     * @param force if true, unexports the object even if there are
     *              pending or in-progress calls; if false, only unexports the object
     *              if there are no pending or in-progress calls
     * @return true if operation is successful, false otherwise
     */
    public static boolean unexportObject(Remote obj, boolean force) {
        return module.unexportObject(obj, force);
    }

    private static final ThreadLocal<Integer> timeoutLocal = new ThreadLocal<Integer>();
    private static final int DEFAULT_TIMEOUT = 60000;

    /**
     * 设置本次方法调用的超时时间，线程相关
     *
     * @param timeout 超时时间
     * @return 上一次的超时时间
     */
    public static int setTimeout(int timeout) {
        Integer i = timeoutLocal.get();
        timeoutLocal.set(timeout);
        return i != null ? i : DEFAULT_TIMEOUT;
    }

    /**
     * 返回当前的超时时间
     */
    public static int getTimeout() {
        Integer i = timeoutLocal.get();
        return i != null ? i : DEFAULT_TIMEOUT;
    }

    /**
     * Returns the stub for the remote object <b>obj</b> passed
     * as a parameter. This operation is only valid <i>after</i>
     * the object has been exported.
     *
     * @return the stub for the remote object, <b>obj</b>.
     * @throws NoSuchObjectException if the stub for the
     *                                        remote object could not be found.
     */
    public static Remote getStub(Object impl) throws NoSuchObjectException {
        return module.getStub(impl);
    }
}
