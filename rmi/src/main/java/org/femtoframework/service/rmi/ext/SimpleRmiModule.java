package org.femtoframework.service.rmi.ext;

import org.femtoframework.service.rmi.RmiModule;
import org.femtoframework.service.rmi.StrOID;
import org.femtoframework.service.rmi.server.ObjectTable;
import org.femtoframework.service.rmi.server.UnicastServerRef;
import org.femtoframework.util.StringUtil;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;

/**
 * Rmi模块实现
 *
 * @author fengyun
 * @version 1.00 2005-5-22 22:21:53
 */
public class SimpleRmiModule implements RmiModule
{
//    /**
//     * 类装载器
//     */
//    private final Map<ClassLoader, RemoteLoader> loaders = new HashMap<>();

//    /**
//     * 根据给定的类转载器返回响应的远程装载器
//     *
//     * @param loader 类装载器
//     * @return 远程资源转载器
//     */
//    public RemoteLoader getRemoteLoader(ClassLoader loader)
//    {
//        RemoteLoader remoteLoader = loaders.get(loader);
//        if (remoteLoader == null) {
//            synchronized (loaders) {
//                remoteLoader = loaders.get(loader);
//                if (remoteLoader == null) {
//                    RemoteLoader rl = new SimpleRemoteLoader(loader);
//                    exportObject(rl);
//                    loaders.put(loader, rl);
//                    remoteLoader = rl;
//                }
//            }
//        }
//        return remoteLoader;
//    }

    /**
     * 判断对象是否已经导出
     *
     * @param obj 对象
     */
    public boolean isExported(Object obj)
    {
        return ObjectTable.getTarget(obj) != null;
    }

//    /**
//     * 创建对象并且导出
//     *
//     * @param className
//     * @return
//     * @throws org.femtoframework.service.rmi.RemoteException
//     *          导出的时候的异常
//     * @throws org.femtoframework.lang.reflect.NoSuchClassException
//     *
//     */
//    public Remote exportObject(String className)
//    {
//        Object obj = Reflection.newInstance(className);
//        return exportObject(obj);
//    }
//    /**
//     * Export the remote object to make it available to receive incoming calls,
//     * using an anonymous port.
//     *
//     * @param obj the remote object to be exported
//     * @return remote object stub
//     * @throws org.femtoframework.service.rmi.RemoteException
//     *          if export fails
//     */
//    public Remote exportObject(Object obj)
//    {
//        return exportObject(obj, false);
//    }

//    public Remote exportObject(Object obj, boolean permanent)
//    {
//        UnicastServerRef serverRef = new UnicastServerRef(new LongOID());
//        return serverRef.exportObject(obj, null, permanent);
//    }

    /**
     * Export the remote object to make it available to receive incoming calls,
     * using an anonymous port.
     * 使用StrOID方式导出对象
     *
     * @param obj the remote object to be exported
     * @param uri 导出的名称
     * @return remote object stub
     * @throws org.femtoframework.service.rmi.RemoteException
     *          if export fails
     * @throws org.femtoframework.service.rmi.ExportException
     *          if export fails
     * @see org.femtoframework.service.rmi.StrOID
     */
    public Remote exportObject(Object obj, String uri)
    {
        //名称无效则使用id方式导出
        if (StringUtil.isInvalid(uri)) {
            throw new IllegalArgumentException("Invalid uri:" + uri);
        }
        else {
            UnicastServerRef serverRef = new UnicastServerRef(new StrOID(uri));
            return serverRef.exportObject(obj, null);
        }
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
     * @since 1.2
     */
    public boolean unexportObject(Remote obj, boolean force)
    {
        return ObjectTable.unexportObject(obj, force);
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
    public Remote getStub(Object impl) throws NoSuchObjectException
    {
        return ObjectTable.getStub(impl);
    }
}
