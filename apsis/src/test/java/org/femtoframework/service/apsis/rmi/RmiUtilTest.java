package org.femtoframework.service.apsis.rmi;

import org.femtoframework.service.rmi.RmiUtil;
import org.junit.Test;

/**
 * RmiUtilTest
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-12-21 15:24:31
 */
public class RmiUtilTest {


    @Test
    public void testExport() throws Exception {
        SayHello orig = new SayHelloImpl();
        SayHello hello = (SayHello) RmiUtil.exportObject(orig, "default:hello");
        hello.sayHello();
    }
}
