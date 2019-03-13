package org.femtoframework.service.apsis.session;

/**
 * 需要SessionContainer
 *
 * @author fengyun
 * @version 1.00 2005-11-11 14:44:57
 */
public interface SessionContainerAware
{
    /**
     * 注入SessionContainer
     *
     * @param container SessionContainer
     */
    void setSessionContainer(SessionContainer container);
}
