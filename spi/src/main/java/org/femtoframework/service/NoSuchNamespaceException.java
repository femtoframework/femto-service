package org.femtoframework.service;

/**
 * No such namespace exception
 *
 * @author fengyun
 * @version 1.00 2005-9-6 22:03:30
 */
public class NoSuchNamespaceException extends FrameworkException
{
    /**
     * 构造
     *
     * @param namespace Namespace
     */
    public NoSuchNamespaceException(String namespace)
    {
        super(Response.SC_NAMESPACE_NOT_FOUND, "no_such_namespace", namespace);
    }
}
