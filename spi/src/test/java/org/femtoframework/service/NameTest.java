package org.femtoframework.service;

import org.femtoframework.coin.CoinConstants;
import org.femtoframework.coin.naming.CoinNamingParser;
import org.junit.Test;

import javax.naming.Name;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author fengyun
 * @version 1.00 2005-6-12 23:45:03
 */
public class NameTest
{
    @Test
    public void testName() throws Exception
    {
        CoinNamingParser parser = new CoinNamingParser(CoinConstants.CHAR_SLASH);

        Name name = parser.parse("!192.168.6.233:9168/webmail/webmail");
        assertEquals("!192.168.6.233", name.get(0));
        name = parser.parse("+/webmail/webmail");
        assertEquals("+/webmail/webmail", name.toString());
        assertEquals("+", name.get(0));
        name = parser.parse("*/webmail/webmail");
        assertEquals("*/webmail/webmail", name.toString());
        assertEquals("*", name.get(0));

        name = parser.parse("#webmail/webmail/webmail");
        assertEquals("#webmail", name.get(0));
    }
}
