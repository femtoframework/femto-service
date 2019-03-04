package org.femtoframework.service.apsis.marshal;

import org.femtoframework.io.FastObjectOutputStream;
import org.femtoframework.service.rmi.RemoteStub;
import org.femtoframework.service.rmi.StrOID;
import org.femtoframework.service.rmi.server.ObjectTable;
import org.femtoframework.service.rmi.server.Target;
import org.femtoframework.service.rmi.server.UnicastServerRef;
import org.femtoframework.util.StringUtil;

import javax.annotation.ManagedBean;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.Remote;

/**
 * Rmi对象输出流，能够将实现Remote或者添加Remote注释的类自动导出和替换成Stub
 *
 * @author fengyun
 * @version 1.00 2005-6-4 20:41:53
 */
public class ObjectMarshalStream extends FastObjectOutputStream {
    /**
     * Creates an ObjectOutputStream that writes to the specified OutputStream.
     * This constructor writes the serialization stream header to the
     * underlying stream; callers may wish to flush the stream immediately to
     * ensure that constructors for receiving ObjectInputStreams will not block
     * when reading the header.
     * <p/>
     * <p>If a security manager is installed, this constructor will check for
     * the "enableSubclassImplementation" SerializablePermission when invoked
     * directly or indirectly by the constructor of a subclass which overrides
     * the ObjectOutputStream.putFields or ObjectOutputStream.writeUnshared
     * methods.
     *
     * @param out output stream to write to
     * @throws IOException  if an I/O error occurs while writing stream header
     * @throws SecurityException    if untrusted subclass illegally overrides
     *                              security-sensitive methods
     * @throws NullPointerException if <code>out</code> is <code>null</code>
     * @see java.io.ObjectOutputStream#ObjectOutputStream()
     * @see java.io.ObjectOutputStream#putFields()
     * @see java.io.ObjectInputStream#ObjectInputStream(java.io.InputStream)
     */
    public ObjectMarshalStream(OutputStream out)
        throws IOException {
        super(out);
        enableReplaceObject(true);
    }

    /**
     * This method will allow trusted subclasses of ObjectOutputStream to
     * substitute one object for another during serialization. Replacing
     * objects is disabled until enableReplaceObject is called. The
     * enableReplaceObject method checks that the stream requesting to do
     * replacment can be trusted.  The first occurrence of each object written
     * into the serialization stream is passed to replaceObject.  Subsequent
     * references to the object are replaced by the object returned by the
     * original call to replaceObject.  To ensure that the private state of
     * objects is not unintentionally exposed, only trusted streams may use
     * replaceObject.
     * <p/>
     * <p>The ObjectOutputStream.writeObject method takes a parameter of type
     * Object (as opposed to type Serializable) to allow for cases where
     * non-serializable objects are replaced by serializable ones.
     * <p/>
     * <p>When a subclass is replacing objects it must insure that either a
     * complementary substitution must be made during deserialization or that
     * the substituted object is compatible with every field where the
     * reference will be stored.  Objects whose type is not a subclass of the
     * type of the field or array element abort the serialization by raising an
     * exception and the object is not be stored.
     * <p/>
     * <p>This method is called only once when each object is first
     * encountered.  All subsequent references to the object will be redirected
     * to the new object. This method should return the object to be
     * substituted or the original object.
     * <p/>
     * <p>Null can be returned as the object to be substituted, but may cause
     * NullReferenceException in classes that contain references to the
     * original object since they may be expecting an object instead of
     * null.
     *
     * @param obj the object to be replaced
     * @return the alternate object that replaced the specified one
     * @throws IOException Any exception thrown by the underlying
     *                     OutputStream.
     */
    protected Object replaceObject(Object obj) throws IOException {
        if (obj != null) {
            Class clazz = obj.getClass();

            if (clazz.isAnnotationPresent(ManagedBean.class)) { //如果是业务对象，那么可以自动导出
                Target target = ObjectTable.getTarget(obj);
                if (target == null) {
                    ManagedBean bo = (ManagedBean)clazz.getAnnotation(ManagedBean.class);
                    //自动导出
                    String value = bo.value();
                    UnicastServerRef serverRef;
                    if (StringUtil.isValid(value)) {
                        serverRef = new UnicastServerRef(new StrOID(value));
                        obj = serverRef.exportObject(obj, null);
                    }
                }
                else {
                    obj = target.getStub();
                }
            }
            else if ((obj instanceof Remote) && !(obj instanceof RemoteStub)) {
                Target target = ObjectTable.getTarget(obj);
                if (target != null) {
                    obj = target.getStub();
                }
            }
        }
        return obj;
    }
}
