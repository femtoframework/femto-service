package org.femtoframework.service.apsis.rmi;

import java.rmi.Remote;

/**
 * @author fengyun
 * @version 1.00 2005-5-21 2:09:41
 */
public interface SayHello extends Remote
{
    public void sayHello();
}
