package org.femtoframework.service.apsis.coin;

import org.femtoframework.naming.ContextObjectFactory;
import org.femtoframework.naming.NamingService;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import java.util.Hashtable;

/**
 * 采用COIN名字解析器的工厂
 *
 * @author fengyun
 * @version 1.00 2005-6-11 17:15:56
 */
public class CoinContextObjectFactory extends ContextObjectFactory
{
    protected Context createNamingContext(Hashtable env, Name name,
                                          NamingService service)
        throws NamingException
    {
        return new CoinNamingContext(env, name, null);
    }
}
