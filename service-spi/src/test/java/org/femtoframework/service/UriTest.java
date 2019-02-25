package org.femtoframework.service;


import java.lang.reflect.Array;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 *
 * @author fengyun
 * @version 1.00 2005-6-12 23:30:10
 */
public class UriTest
{
    @org.junit.Test
    public void testUri() throws Exception
    {
        URI uri = new URI("coin://192.168.6.233:9168/webmail/webmail");
        assertEquals("192.168.6.233", uri.getHost());
        uri = new URI("coin://+/webmail/webmail");
        assertEquals("coin://+/webmail/webmail", uri.toString());
        assertEquals("+", uri.getAuthority());
        assertNull(uri.getHost());
        uri = new URI("coin://*/webmail/webmail");
        assertEquals("coin://*/webmail/webmail", uri.toString());
        assertEquals("*", uri.getAuthority());
        assertNull(uri.getHost());
    }

    @org.junit.Test
    public void testConvert()
    {
        Test<UriTest> test = new Test<UriTest>();
        UriTest[] t = test.create(UriTest.class);
        assertNotNull(t);
    }

    public class Test<T>
    {
        public T[] create(Class<T> clazz)
        {
            return (T[])Array.newInstance(clazz, 2);
        }
    }
}
