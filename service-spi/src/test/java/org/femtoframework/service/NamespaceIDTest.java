package org.femtoframework.service;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author fengyun
 * @version 1.00 2005-5-22 11:37:29
 */
public class NamespaceIDTest
{
    @Test
    public void testServiceID() throws Exception
    {
        ServerID sid = new ServerID("192.168.6.248", 9168, 51);
        NamespaceID namespaceId = new NamespaceID(sid.getId(), 88);
        assertEquals(namespaceId.getServerId(), sid.getId());
    }

    @Test
    public void testToString() throws Exception
    {
        ServerID sid = new ServerID("192.168.6.248", 9168, 51);
        NamespaceID namespaceId = new NamespaceID(sid.getId(), 88);
        assertEquals("192.168.6.248:9168:51:88", namespaceId.toString());
    }
}