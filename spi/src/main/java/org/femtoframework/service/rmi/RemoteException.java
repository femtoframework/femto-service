package org.femtoframework.service.rmi;

/**
 * 远程服务器异常<br>
 * 跟java.rmi.RemoteException不同的是它继承RuntimeException，<br>
 * 因为Remote要求所有的方法上面都增加java.rmi.RemoteException的方法抛出，<br>
 * 而这对程序来说是极其难看的，因为对调用者来说根本不关心我的对象是本地的还是远程的，<br>
 * 所以这里我们采用了运行期异常，当发生连接问题的时候抛出该异常<br>
 *
 * @author fengyun
 * @version 1.00 2005-5-21 1:49:26
 */
public class RemoteException extends RuntimeException
{
    /**
     * Constructs a new runtime exception with the specified cause and a
     * detail message of <tt>(cause==null ? null : cause.toString())</tt>
     * (which typically contains the class and detail message of
     * <tt>cause</tt>).  This constructor is useful for runtime exceptions
     * that are little more than wrappers for other throwables.
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A <tt>null</tt> value is
     *              permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.4
     */
    public RemoteException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public RemoteException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * <code>cause</code> is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A <tt>null</tt> value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @since 1.4
     */
    public RemoteException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
