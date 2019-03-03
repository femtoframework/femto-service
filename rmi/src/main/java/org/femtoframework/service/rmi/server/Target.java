package org.femtoframework.service.rmi.server;

import org.femtoframework.service.rmi.Invocation;
import org.femtoframework.service.rmi.ObjID;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.security.AccessControlContext;
import java.security.AccessController;

/**
 * A target contains information pertaining to a remote object that
 * resides in this address space.  Targets are located via the
 * ObjectTable.
 */
public final class Target
{
    /**
     * object id for target
     */
    private ObjID id;

    /**
     * weak reference to remote object implementation
     */
    private WeakRef weakImpl;
    /**
     * dispatcher for remote object
     */
    private Invocation disp;
    /**
     * stub for remote object
     */
    private Remote stub;

//    /**
//     * set of clients that hold references to this target
//     */
//    private Vector<VMID> refSet = new Vector<>();
//
//    /**
//     * table that maps client endpoints to sequence numbers
//     */
//    private Hashtable<VMID, SequenceEntry> sequenceTable = new Hashtable<>(5);

    /**
     * access control context in which target was created
     */
    private AccessControlContext acc;
    /**
     * context class loader in which target was created
     */
    private ClassLoader ccl;
    /**
     * number of pending/executing calls
     */
    private int callCount = 0;
//    /**
//     * number to identify next callback thread created here
//     */
//    private static int nextThreadNum = 0;

    /**
     * Construct a Target for a remote object "impl" with
     * a specific object id.
     * <p/>
     */
    public Target(Object impl, Invocation disp, Remote stub, ObjID id)
    {
        this.weakImpl = new WeakRef(impl, ObjectTable.reapQueue);
        this.disp = disp;
        this.stub = stub;
        this.id = id;
        this.acc = AccessController.getContext();

        /*
         * Fix for 4149366: so that downloaded parameter types unmarshalled
         * for this impl will be compatible with types known only to the
         * impl class's class loader (when it's not identical to the
         * exporting thread's context class loader), mark the impl's class
         * loader as the loader to use as the context class loader in the
         * server's dispatch thread while a call to this impl is being
         * processed (unless this exporting thread's context class loader is
         * a child of the impl's class loader, such as when a registry is
         * exported by an application, in which case this thread's context
         * class loader is preferred).
         */
        ClassLoader threadContextLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader serverLoader = impl.getClass().getClassLoader();
        if (checkLoaderAncestry(threadContextLoader, serverLoader)) {
            this.ccl = threadContextLoader;
        }
        else {
            this.ccl = serverLoader;
        }

        pinImpl();
    }

    /**
     * Return true if the first class loader is a child of (or identical
     * to) the second class loader.  Either loader may be "null", which is
     * considered to be the parent of any non-null class loader.
     * <p/>
     * (utility method added for the 1.2beta4 fix for 4149366)
     */
    private static boolean checkLoaderAncestry(ClassLoader child,
                                               ClassLoader ancestor)
    {
        if (ancestor == null) {
            return true;
        }
        else if (child == null) {
            return false;
        }
        else {
            for (ClassLoader parent = child;
                 parent != null;
                 parent = parent.getParent()) {
                if (parent == ancestor) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Get the stub (proxy) object for this target
     */
    public Remote getStub()
    {
        return stub;
    }

    /**
     * Returns the object id for the target.
     */
    ObjID getObjID()
    {
        return id;
    }

    /**
     * Get the weak reference for the Impl of this target.
     */
    WeakRef getWeakImpl()
    {
        return weakImpl;
    }

    /**
     * Returns the Invocation for this remote object target.
     */
    Invocation getInvocation()
    {
        return disp;
    }

    AccessControlContext getAccessControlContext()
    {
        return acc;
    }

    ClassLoader getContextClassLoader()
    {
        return ccl;
    }

    /**
     * Get the impl for this target.
     * Note: this may return null if the impl has been garbage collected.
     * (currently, there is no need to make this method public)
     */
    synchronized Object getImpl()
    {
        return weakImpl.get();
    }

    /**
     * Pin impl in target. Pin the WeakRef object so it holds a strong
     * reference to the object to it will not be garbage collected locally.
     * This way there is a single object responsible for the weak ref
     * mechanism.
     */
    synchronized void pinImpl()
    {
        weakImpl.pin();
    }

//    /**
//     * Add an endpoint to the remembered set.  Also adds a notifier
//     * to call back if the address space associated with the endpoint
//     * dies.
//     */
//    synchronized void referenced(long sequenceNum, VMID vmid)
//    {
//        // check sequence number for vmid
//        SequenceEntry entry = sequenceTable.get(vmid);
//        if (entry == null) {
//            sequenceTable.put(vmid, new SequenceEntry(sequenceNum));
//        }
//        else if (entry.sequenceNum < sequenceNum) {
//            entry.update(sequenceNum);
//        }
//        else {
//            // late dirty call; ignore.
//            return;
//        }
//
//        if (!refSet.contains(vmid)) {
//            /*
//             * A Target must be pinned while its refSet is not empty.  It may
//             * have become unpinned if external LiveRefs only existed in
//             * serialized form for some period of time, or if a client failed
//             * to renew its lease due to a transient network failure.  So,
//             * make sure that it is pinned here; this fixes bugid 4069644.
//             */
//            pinImpl();
//            if (getImpl() == null)    // too late if impl was collected
//            {
//                return;
//            }
//
//            refSet.addElement(vmid);
////            DGCImpl.getDGCImpl().registerTarget(vmid, this);
//        }
//    }
//
//    /**
//     * Remove endpoint from remembered set.  If set becomes empty,
//     * remove server from Transport's object table.
//     */
//    synchronized void unreferenced(long sequenceNum, VMID vmid, boolean strong)
//    {
//        // check sequence number for vmid
//        SequenceEntry entry = sequenceTable.get(vmid);
//        if (entry == null || entry.sequenceNum > sequenceNum) {
//            // late clean call; ignore
//            return;
//        }
//        else if (strong) {
//            // strong clean call; retain sequenceNum
//            entry.retain(sequenceNum);
//        }
//        else if (!entry.keep) {
//            // get rid of sequence number
//            sequenceTable.remove(vmid);
//        }
//
//        refSetRemove(vmid);
//    }
//
//    /**
//     * Remove endpoint from the reference set.
//     */
//    synchronized private void refSetRemove(VMID vmid)
//    {
//        if (refSet.removeElement(vmid) && refSet.isEmpty()) {
//            // reference set is empty, so server can be garbage collected.
//            // remove object from table.
//
//            /*
//             * If the remote object implements the Unreferenced interface,
//             * invoke its unreferenced callback in a separate thread.
//             */
//            Object obj = getImpl();
//            if (obj instanceof Unreferenced) {
//                final Unreferenced unrefObj = (Unreferenced) obj;
//                final Thread t = (Thread)
//                    AccessController.doPrivileged(new ThreadAction(new Runnable()
//                    {
//                        public void run()
//                        {
//                            unrefObj.unreferenced();
//                        }
//                    }, "Unreferenced-" + nextThreadNum++, false, true));
//                // REMIND: access to nextThreadNum not synchronized; you care?
//                /*
//                 * We must manually set the context class loader appropriately
//                 * for threads that may invoke user code (see bugid 4171278).
//                 */
//                AccessController.doPrivileged((PrivilegedAction) () -> {
//                    t.setContextClassLoader(ccl);
//                    return null;
//                });
//
//                t.start();
//            }
//        }
//    }

    /**
     * Mark this target as not accepting new calls if any of the
     * following conditions exist: a) the force parameter is true,
     * b) the target's call count is zero, or c) the object is already
     * not accepting calls. Returns true if target is marked as not
     * accepting new calls; returns false otherwise.
     */
    synchronized boolean unexport(boolean force)
    {
        if (force || (callCount == 0) || (disp == null)) {
            disp = null;
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Increment call count.
     */
    synchronized void incrementCallCount() throws NoSuchObjectException
    {
        if (disp != null) {
            callCount++;
        }
        else {
            throw new NoSuchObjectException("object not accepting new calls");
        }
    }

    /**
     * Decrement call count.
     */
    synchronized void decrementCallCount()
    {

        if (--callCount < 0) {
            throw new Error("internal error: call count less than zero");
        }
    }

//    /**
//     * Returns true if remembered set is empty; otherwise returns
//     * false
//     */
//    boolean isEmpty()
//    {
//        return refSet.isEmpty();
//    }

//    /**
//     * This method is called if the address space associated with the
//     * vmid dies.  In that case, the vmid should be removed
//     * from the reference set.
//     */
//    synchronized public void vmidDead(VMID vmid)
//    {
//        sequenceTable.remove(vmid);
//        refSetRemove(vmid);
//    }
}

//
//class SequenceEntry
//{
//    long sequenceNum;
//    boolean keep;
//
//    SequenceEntry(long sequenceNum)
//    {
//        this.sequenceNum = sequenceNum;
//        keep = false;
//    }
//
//    void retain(long sequenceNum)
//    {
//        this.sequenceNum = sequenceNum;
//        keep = true;
//    }
//
//    void update(long sequenceNum)
//    {
//        this.sequenceNum = sequenceNum;
//    }
//}

