package org.femtoframework.service.rmi.server;

import java.security.PrivilegedAction;

/*
 * ThreadAction is a utility class for creating threads using
 * AccessController.doPrivileged.
 *
 * The ThreadAction class centralizes the creation of the threads
 * for use by the RMI implementation runtime, so they are all placed
 * in an appropriate thread group.
 *
 * Note that since a thread in the root thread group is created via the
 * ThreadAction class, a public class, it does NOT acquire privileged
 * AccessController status, so it does not give capabilites that they
 * would not otherwise have.  Therefore, code in the RMI runtime that
 * calls ThreadAction should use the doPrivileged construct to
 * create threads.
 */

class ThreadAction implements PrivilegedAction
{

    /**
     * runnable which will be started in the run method of this action
     */
    private Runnable runnable;
    /**
     * name of the thread this action will start
     */
    private String name;
    /**
     * daemonness of the thread started by this action
     */
    private boolean daemon;
    /**
     * dont create a thread in the VM system group
     */
    private boolean nonSystem;
    /**
     * ThreadGroup for RMI runtime implementation's threads;
     * accessed through getThreadGroup() method
     */
    private static ThreadGroup systemGroup;
    /**
     * ThreadGroup whos members do not reside in the system group
     * (e.g. threads which carry out remote call invocations).
     */
    private static ThreadGroup nonSystemGroup;


    public ThreadAction(Runnable runnable, String name, boolean daemon)
    {
        this(runnable, name, daemon, false);
    }

    public ThreadAction(Runnable runnable, String name,
                        boolean daemon, boolean nonSystem)
    {
        this.runnable = runnable;
        this.name = name;
        this.daemon = daemon;
        this.nonSystem = nonSystem;
    }

    /*
     * Create a new thread for RMI runtime.
     */
    public Object run()
    {
        Thread t = new Thread(getThreadGroup(), runnable, "rmi_" + name);
        t.setDaemon(daemon);
        return t;
    }

    /**
     * Return the thread group to use for RMI runtime threads.  If the
     * correct thread group has not yet been created, then create it
     * as a child of the appropriate thread group, the system root
     * group or the current thread group (if the VM is an applet).  If
     * the action is creating an RMI system thread, then directly
     * return the system group (the VM root group).
     */
    private ThreadGroup getThreadGroup()
    {

        /**
         * Fix for 4182104: Threads created by remote method
         * invocations shouldn't run as system threads. Create
         * invocation threads in a group other than the system group
         * so that those threads are able to modify their own thread
         * group.
         */
        if (nonSystem) {
            return getNonSystemGroup();
        }

        /*
         * As long as the implementations of the default SecurityManager's
         * checkAccess() methods protect "system" threads by simply
         * denying access (without permission) to threads in the topmost
         * thread group (whose parent is null), we place RMI threads
         * directly in the system thread group, so that they will be as
         * protected as other system threads (see bugid 4166914).  We
         * would prefer, however, to go back to putting RMI threads in a
         * separate thread group, to organize the threads better and not
         * pollute the top-level thread group.
         */
        return getSystemGroup();
    }

    private static synchronized ThreadGroup getNonSystemGroup()
    {
        if (nonSystemGroup == null) {
            /*
             * If a group does not already exist, create a thread
             * group for RMI runtime call invocation threads.  If this
             * fails for security reasons, at least create an RMI
             * thread group in the current thread's group.
             */
            try {
                final ThreadGroup finalNonSystemParent = getSystemGroup();
                nonSystemGroup = (ThreadGroup)
                    java.security.AccessController.doPrivileged((PrivilegedAction) () -> new ThreadGroup(finalNonSystemParent,
                                           "RMI Runtime"));
            }
            catch (SecurityException e) {
                nonSystemGroup = new ThreadGroup("RMI runtime (applet)");
            }
        }
        return nonSystemGroup;
    }

    /**
     * Obtain the VM system or root group, search up from the current
     * thread's thread group to find the root of the ThreadGroup tree.
     */
    private static synchronized ThreadGroup getSystemGroup()
    {
        ThreadGroup root;

        if (systemGroup == null) {
            root = Thread.currentThread().getThreadGroup();
            ThreadGroup parent;
            while ((parent = root.getParent()) != null) {
                root = parent;
            }
            systemGroup = root;
        }

        return systemGroup;
    }
}
