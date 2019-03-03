package org.femtoframework.service.rmi.server;

import org.femtoframework.net.HostPort;
import org.femtoframework.service.rmi.*;

import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.*;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 服务器端引用
 *
 * @author fengyun
 * @version Feb 23, 2003 7:26:09 PM
 */

public class UnicastServerRef
    extends UnicastRef
    implements ServerRef, Invocation
{
    /**
     * maps method hash (Long) to Method object for each remote method
     */
    private transient Map<Long, Method> methodTable = null;

    /**
     * cache of method tables for remote classes
     */
    private final static Map<Class, SoftReference[]> methodTableCache = new WeakHashMap<>(11);

    /**
     * Create a new (empty) Unicast remote reference.
     */
    public UnicastServerRef()
    {
        super();
    }

    /**
     * 根据对象标识来创建远程引用
     *
     * @param id 对象标识
     */
    public UnicastServerRef(ObjID id)
    {
        super(HostPort.getLocal().getId(), id);
    }

    /**
     * 调用方法
     *
     * @param obj    the target remote object for the call
     * @param params the parameter list
     * @param opnum  a hash that may be used to represent the method
     *               method arguments can be obtained.
     * @throws RemoteException unable to marshal
     *                                  return result
     */
    public Object invoke(Object obj, Object[] params, long opnum)
        throws RemoteException
    {
        Method method = methodTable.get(opnum);
        if (method == null) {
            throw new UnmarshalException("invalid method hash");
        }

        // make upcall on remote object
        try {
            return method.invoke(obj, params);
        }
        catch (IllegalAccessException | IllegalArgumentException iae) {
            throw new RemoteException(iae.getMessage());
        }
        catch (InvocationTargetException ite) {
            Throwable t = ite.getTargetException();
            if (t instanceof RemoteException) {
                throw(RemoteException) t;
            }
            else {
                throw new RemoteException("Invocation Exception", t);
            }
        }
        catch (Throwable t) {
            if (t instanceof Error) {
                throw new ServerError("Error occurred in server thread", (Error) t);
            }
            else {
                throw new RemoteException("InvocationException", t);
            }
        }
    }

    /**
     * Export this object, create the skeleton and stubs for this
     * dispatcher.  Create a stub based on the type of the impl,
     * initialize it with the appropriate remote reference. Create the
     * target defined by the impl, dispatcher (this) and stub.
     * Export that target via the Ref.
     *
     * @param impl  the remote object implementation
     * @param data information necessary to export the object
     * @return the stub for the remote object
     * @throws org.femtoframework.service.rmi.RemoteException
     *          if an exception occurs attempting
     *          to export the object (e.g., stub class could not be found)
     */
    public Remote exportObject(Object impl, Object data)
    {
        Remote stub;
        Target target = ObjectTable.getTarget(impl);
        if (target == null) {
            stub = getStub(impl);
            target = new Target(impl, this, stub, id);
            target = ObjectTable.putTarget(target);
            stub = target.getStub();
        }
        else {
            stub = target.getStub();
            if (!target.getObjID().equals(id)) {
                target = new Target(impl, this, stub, id);
                target = ObjectTable.putTarget(target);
            }
        }
        methodTable = getMethodTable(impl.getClass());
        return stub;
    }

    /**
     * Returns the appropriate stub for the impl.
     */
    public Remote getStub(Object impl)
    {
        return StubUtil.createStub(impl.getClass(), getClientRef());
    }

    /**
     * Return the client remote reference for this remoteRef.
     * In the case of a client RemoteRef "this" is the answer.
     * For a server remote reference, a client side one will have to
     * found or created.
     */
    protected RemoteRef getClientRef()
    {
        return new UnicastRef(vmid, id);
    }

    /**
     * Get the method table for the given remote class.
     * <p/>
     * Method tables for remote classes are cached in a hash table using
     * weak references to hold the Class object keys, so that the cache
     * does not prevent the class from being unloaded, and using soft
     * references to hold the values, so that the computed method tables
     * will generally persist while no objects of the remote class are
     * exported, but their storage may be reclaimed if necessary, and
     * accidental reachability of the remote class through its interfaces
     * is avoided.
     */
    private static Map<Long, Method> getMethodTable(Class remoteClass)
    {
        SoftReference[] tableRef;

        synchronized (methodTableCache) {
            /*
             * Look up class in cache; add entry if not found.
             */
            tableRef = methodTableCache.get(remoteClass);
            if (tableRef == null) {
                tableRef = new SoftReference[]{null};
                methodTableCache.put(remoteClass, tableRef);
            }
        }

        /*
         * Check cached reference to method table for this class;
         * if it is null, go and create the table.
         */
        synchronized (tableRef) {
            Map<Long, Method> table = null;
            if (tableRef[0] != null) {
                table = (Map<Long, Method>) tableRef[0].get();
            }
            if (table == null) {
                table = createMethodTable(remoteClass);
                tableRef[0] = new SoftReference<Map<Long, Method>>(table);
            }
            return table;
        }
    }

    /*
     * The following static methods are related to the creation of the
     * "method table" for a remote class, which maps method hashes to
     * the appropriate Method objects for the class's remote methods.
     */

    /**
     * Create a hash table that contains, for each remote method in the
     * given class, a mapping from its method hash (stored in a instance
     * of java.lang.Long) to its corresponding java.lang.reflect.Method
     * object.
     */
    private static Map<Long, Method> createMethodTable(Class remoteClass)
    {
        /*
         * The table is entirely created within this method (in a single
         * thread), and it should only be read subsequently, so using the
         * unsynchronized HashMap type and not synchronizing for lookups
         * is safe.
         */
        Map<Long, Method> table = new HashMap<Long, Method>();

        /*
         * For each interface directly implemented by
         * this class or any of its superclasses...
         */
        for (Class cl = remoteClass; cl != null; cl = cl.getSuperclass()) {
            Class[] interfaces = cl.getInterfaces();
            for (Class anInterface : interfaces) {
                /*
                 * ...if it is a remote interface (if it extends from
                 * java.rmi.Remote), then add all of its methods to the
                 * method table.
                 */
//                if (Remote.class.isAssignableFrom(interfaces[i])) {
                Method methods[] = anInterface.getMethods();
                for (final Method m : methods) {
                    /*
                    * Set this Method object to override language
                    * access checks so that the dispatcher can invoke
                    * methods from non-public remote interfaces.
                    */
                    AccessController.doPrivileged((PrivilegedAction) () -> {
                        m.setAccessible(true);
                        return null;
                    });
                    long hash = MethodUtil.hashCode(m);
                    table.put(hash, m);
                }
            }
//            }
        }
        return table;
    }
}
