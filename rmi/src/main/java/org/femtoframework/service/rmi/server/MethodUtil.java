package org.femtoframework.service.rmi.server;

import org.femtoframework.io.ByteArrayOutputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.*;
import java.lang.reflect.Method;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Method工具
 *
 * @author fengyun
 * @version Feb 13, 2003 11:16:22 PM
 */
public class MethodUtil
{

    /**
     * Compute the "method hash" of a remote method.  The method hash
     * is a long containing the first 64 bits of the SHA digest from
     * the UTF encoded string of the method name and descriptor.
     */
    public static long hashCode(Method m)
    {
        long hash = 0;
        try (ByteArrayOutputStream sink = new ByteArrayOutputStream()){
            MessageDigest md = MessageDigest.getInstance("SHA");
            DataOutputStream out = new DataOutputStream(new DigestOutputStream(sink, md));

            String s = getMethodNameAndDescriptor(m);
            out.writeUTF(s);

            // use only the first 64 bits of the digest for the hash
            out.flush();
            byte hasharray[] = md.digest();
            for (int i = 0; i < Math.min(8, hasharray.length); i++) {
                hash += ((long)(hasharray[i] & 0xFF)) << (i * 8);
            }
        }
        catch (IOException ignore) {
            /* can't happen, but be deterministic anyway. */
            hash = -1;
        }
        catch (NoSuchAlgorithmException complain) {
            throw new SecurityException(complain.getMessage());
        }
        return hash;
    }

    /**
     * Return a string consisting of the given method's name followed by
     * its "method descriptor", as appropriate for use in the computation
     * of the "method hash".
     * <p/>
     * See section 4.3.3 of The Java Virtual Machine Specification for
     * the definition of a "method descriptor".
     */
    private static String getMethodNameAndDescriptor(Method m)
    {
        StringBuilder desc = new StringBuilder(m.getName());
        desc.append('(');
        Class[] paramTypes = m.getParameterTypes();
        for (Class paramType : paramTypes) {
            desc.append(getTypeDescriptor(paramType));
        }
        desc.append(')');
        Class returnType = m.getReturnType();
        if (returnType == void.class) {    // optimization: handle void here
            desc.append('V');
        }
        else {
            desc.append(getTypeDescriptor(returnType));
        }
        return desc.toString();
    }

    /**
     * Get the descriptor of a particular type, as appropriate for either
     * a parameter or return type in a method descriptor.
     */
    private static String getTypeDescriptor(Class type)
    {
        if (type.isPrimitive()) {
            if (type == int.class) {
                return "I";
            }
            else if (type == boolean.class) {
                return "Z";
            }
            else if (type == byte.class) {
                return "B";
            }
            else if (type == char.class) {
                return "C";
            }
            else if (type == short.class) {
                return "S";
            }
            else if (type == long.class) {
                return "J";
            }
            else if (type == float.class) {
                return "F";
            }
            else if (type == double.class) {
                return "D";
            }
            else if (type == void.class) {
                return "V";
            }
            else {
                throw new Error("unrecognized primitive type: " + type);
            }
        }
        else if (type.isArray()) {
            /*
             * According to JLS 20.3.2, the getName() method on Class does
             * return the VM type descriptor format for array classes (only);
             * using that should be quicker than the otherwise obvious code:
             *
             *     return "[" + getTypeDescriptor(type.getComponentType());
             */
            return type.getName().replace('.', '/');
        }
        else {
            return "L" + type.getName().replace('.', '/') + ";";
        }
    }


//    /**
//     * <p>Return an accessible method (that is, one that can be invoked via
//     * reflection) that implements the specified Method.  If no such method
//     * can be found, return <code>null</code>.</p>
//     *
//     * @param method The method that we wish to call
//     */
//    public static Method getAccessibleMethod(Method method)
//    {
//        // Make sure we have a method to check
//        if (method == null) {
//            return (null);
//        }
//
//        // If the requested method is not public we cannot call it
//        if (!Modifier.isPublic(method.getModifiers())) {
//            return (null);
//        }
//
//        // If the declaring class is public, we are done
//        Class clazz = method.getDeclaringClass();
//        if (Modifier.isPublic(clazz.getModifiers())) {
//            return (method);
//        }
//
//        // Check the implemented interfaces and subinterfaces
//        String methodName = method.getName();
//        Class[] paramTypes = method.getParameterTypes();
//        method =
//                getAccessibleMethodFromInterfaceNest(clazz,
//                        methodName, paramTypes);
//        return (method);
//    }
//
//    /**
//     * <p>Return an accessible method (that is, one that can be invoked via
//     * reflection) that implements the specified method, by scanning through
//     * all implemented interfaces and subinterfaces.  If no such method
//     * can be found, return <code>null</code>.</p>
//     * <p/>
//     * <p> There isn't any good reason why this method must be private.
//     * It is because there doesn't seem any reason why other classes should
//     * call this rather than the higher level methods.</p>
//     *
//     * @param clazz          Parent class for the interfaces to be checked
//     * @param methodName     Method name of the method we wish to call
//     * @param parameterTypes The parameter type signatures
//     */
//    private static Method getAccessibleMethodFromInterfaceNest(Class clazz, String methodName, Class parameterTypes[])
//    {
//
//        Method method = null;
//
//        // Search up the superclass chain
//        for (; clazz != null; clazz = clazz.getSuperclass()) {
//
//            // Check the implemented interfaces of the parent class
//            Class<?> interfaces[] = clazz.getInterfaces();
//            for (Class<?> anInterface : interfaces) {
//
//                // Is this interface public?
//                if (!Modifier.isPublic(anInterface.getModifiers())) {
//                    continue;
//                }
//
//                // Does the method exist on this interface?
//                try {
//                    method = anInterface.getDeclaredMethod(methodName, parameterTypes);
//                }
//                catch (java.lang.NoSuchMethodException e) {
//                }
//                if (method != null) {
//                    break;
//                }
//
//                // Recursively check our parent interfaces
//                method = getAccessibleMethodFromInterfaceNest(anInterface, methodName, parameterTypes);
//                if (method != null) {
//                    break;
//                }
//            }
//        }
//
//        // If we found a method return it
//        return (method);
//    }
}