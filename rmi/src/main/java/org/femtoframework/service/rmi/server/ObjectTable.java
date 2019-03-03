package org.femtoframework.service.rmi.server;

import org.femtoframework.service.rmi.ExportException;
import org.femtoframework.service.rmi.ObjID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.ReferenceQueue;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.util.HashMap;
import java.util.Map;

/**
 * Object table shared by all implementors of the Transport interface.
 * This table maps object ids to remote object targets in this address
 * space.
 *
 * @author Ann Wollrath
 * @author Peter Jones
 * @version 1.22, 02/02/00
 */
public class ObjectTable
{
    private static Logger log = LoggerFactory.getLogger(ObjectTable.class);


    /**
     * lock to enforce mutual exclusion on the following static fields:
     * objTable, implTable, lookupKey, keepAliveCount, reaper
     */
    private static final Object lock = new Object();

    /**
     * tables mapping to Target, keyed from ObjID and impl object
     */
    private static Map<ObjID, Target> objTable = new HashMap<ObjID, Target>();
    private static Map<WeakRef, Target> implTable = new HashMap<WeakRef, Target>();
//
//    /**
//     * count of objects in the table, excluding permanent entries
//     */
//    private static int keepAliveCount = 0;
//
//    /**
//     * thread to collect unreferenced objects from table
//     */
//    private static Thread reaper = null;

    /**
     * queue notified when weak refs in the table are cleared
     */
    static ReferenceQueue reapQueue = new ReferenceQueue();

    /*
     * Disallow anyone from creating one of these.
     */
    private ObjectTable()
    {
    }

    /**
     * Returns the target associated with the object id.
     */
    static Target getTarget(ObjID id)
    {
        synchronized (lock) {
            return objTable.get(id);
        }
    }

//    /**
//     * Returns the target associated with the object id.
//     */
//    static Target removeTarget(ObjID id)
//    {
//        synchronized (lock) {
//            return objTable.remove(id);
//        }
//    }

    /**
     * Returns the target associated with the remote object
     */
    public static Target getTarget(Object impl)
    {
        synchronized (lock) {
            return implTable.get(new WeakRef(impl));
        }
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
    public static Remote getStub(Object impl)
        throws NoSuchObjectException
    {
        Target target = getTarget(impl);
        if (target == null) {
            throw new NoSuchObjectException("object not exported");
        }
        else {
            return target.getStub();
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
     */
    public static boolean unexportObject(Remote obj, boolean force)
    {
        synchronized (lock) {
            Target target = getTarget(obj);
            if (target == null) {
                return false;
            }
            else {
                if (target.unexport(force)) {
                    removeTarget(target);
                    return true;
                }
                else {
                    return false;
                }
            }
        }
    }

    /**
     * Add target to object table.  If it is not a permanent entry, then
     * make sure that reaper thread is running to remove collected entries
     * and keep VM alive.
     */
    public static Target putTarget(Target target) throws ExportException
    {
        ObjID id = target.getObjID();
        WeakRef weakImpl = target.getWeakImpl();

        Object impl = target.getImpl();
        if (impl == null) {
            throw new ExportException("internal error: attempt to export collected object");
        }

        synchronized (lock) {
            if (objTable.containsKey(id)) {
                throw new ExportException("internal error: ObjID already in use:" + id);
            }

            objTable.put(id, target);

            if (!implTable.containsKey(weakImpl)) {
                implTable.put(weakImpl, target);
            }
        }

        return  target;
    }

    /**
     * Remove target from object table.  If it was the last non-permanent
     * entry, then kill the reaper thread so the VM can exit.
     * <p/>
     * NOTE: This method must only be invoked while synchronized on
     * the "lock" object, because it does not do so itself.
     */
    private static void removeTarget(Target target)
    {
        ObjID id = target.getObjID();
        if (log.isDebugEnabled()) {
            log.debug("remove target from object table:" + id);
        }
        WeakRef weakImpl = target.getWeakImpl();

        objTable.remove(id);
        implTable.remove(weakImpl);
    }

//    /**
//     * Process client VM signalling reference for given ObjID: forward to
//     * correspoding Target entry.  If ObjID is not found in table,
//     * no action is taken.
//     */
//    static void referenced(ObjID id, long sequenceNum, VMID vmid)
//    {
//        synchronized (lock) {
//            Target target = objTable.get(id);
//            if (target != null) {
//                target.referenced(sequenceNum, vmid);
//            }
//        }
//    }
//
//    /**
//     * Process client VM dropping reference for given ObjID: forward to
//     * correspoding Target entry.  If ObjID is not found in table,
//     * no action is taken.
//     */
//    static void unreferenced(ObjID id, long sequenceNum, VMID vmid,
//                             boolean strong)
//    {
//        synchronized (lock) {
//            Target target = objTable.get(id);
//            if (target != null) {
//                target.unreferenced(sequenceNum, vmid, strong);
//            }
//        }
//    }
}
