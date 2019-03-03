package org.femtoframework.service.rmi;

import org.femtoframework.io.Streamable;

import java.io.Externalizable;

/**
 * 对象标识
 *
 * @author fengyun
 * @version 1.00 2005-8-14 0:40:53
 */
public interface ObjID extends Streamable, Externalizable {
    /**
     * Returns the hash code for the <code>ObjID</code> (the object number).
     */
    int hashCode();

    /**
     * Two object identifiers are considered equal if they have the
     * same contents.
     *
     * @since JDK1.1
     */
    boolean equals(Object obj);

    /**
     * 字符串信息
     *
     * @return 字符串信息
     */
    String toString();
}
