package org.femtoframework.service.rmi;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;

/**
 * Rmi模块
 *
 * @author fengyun
 * @version 1.00 2005-5-22 22:19:00
 */
public interface RmiModule
{
//    /**
//     * 根据给定的类转载器返回响应的远程装载器
//     *
//     * @param loader 类装载器
//     * @return Remote Class Loader
//     */
//    RemoteLoader getRemoteLoader(ClassLoader loader);

    /**
     * 判断对象是否已经导出
     *
     * @param obj 对象
     */
    boolean isExported(Object obj);

    /**
     * Export the remote object to make it available to receive incoming calls,
     * using an anonymous port.
     * 使用StrOID方式导出对象
     *
     * @param obj the remote object to be exported
     * @param uri 导出的名称
     * @return remote object stub
     * @throws RemoteException if export fails
     * @throws ExportException if export fails
     * @since JDK1.1
     * @see StrOID
     */
    Remote exportObject(Object obj, String uri);

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
    boolean unexportObject(Remote obj, boolean force);

    /**
     * Returns the stub for the remote object <b>obj</b> passed
     * as a parameter. This operation is only valid <i>after</i>
     * the object has been exported.
     *
     * @return the stub for the remote object, <b>obj</b>.
     * @throws NoSuchObjectException if the stub for the
     *                                        remote object could not be found.
     */
    Remote getStub(Object impl) throws NoSuchObjectException;
}
