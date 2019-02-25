package org.femtoframework.service;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author fengyun
 * @version 1.00 2005-5-22 11:07:31
 */
public class ServerIDTest
{
    @Test
    public void testServerID() throws Exception
    {
        ServerID id1 = new ServerID("192.168.6.248", 9168);
        System.out.println("ID=" + id1.getId());
        ServerID id2 = new ServerID("192.168.6.248", 9168, 51);
        System.out.println("ID=" + id2.getId());
    }

    @Test
    public void testPerformance() throws Exception
    {
        for (int i = 0; i < 10000; i++) {
            ServerID.toId("192.168.6.248", 9168, 51);
        }
    }

    @Test
    public void testToId() throws Exception
    {
        ServerID id2 = new ServerID("192.168.6.248", 9168, 51);
        System.out.println("ID=" + id2.getId());

        long id = id2.getId();
        ServerID id1 = new ServerID(id);
        assertEquals(id1.getHost(), id2.getHost());
        assertEquals(id1.getPort(), id2.getPort());
        assertEquals(id1.getIdentity(), id2.getIdentity());
    }

    @Test
    public void testClone() throws Exception
    {
        ServerID sid = new ServerID("192.168.6.248", 9168, 51);
        ServerID sid2 = (ServerID) sid.clone();
        assertEquals(sid, sid2);
    }

    @Test
    public void testToString() throws Exception
    {
        ServerID sid = new ServerID("192.168.6.248", 9168, 51);
        assertEquals("192.168.6.248:9168:51", sid.toString());

        ServerID sid2 = new ServerID("192.168.6.248", 9168);
        assertEquals("192.168.6.248:9168", sid2.toString());
    }
}