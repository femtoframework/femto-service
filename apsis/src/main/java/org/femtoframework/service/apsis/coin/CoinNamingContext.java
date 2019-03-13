package org.femtoframework.service.apsis.coin;

import org.femtoframework.coin.naming.CoinNamingParser;
import org.femtoframework.naming.NamingContext;
import org.femtoframework.naming.NamingService;

import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import java.util.Hashtable;

/**
 * 采用COIN名称规则的命名上下文
 *
 * @author fengyun
 * @version 1.00 2005-6-11 17:14:11
 */
public class CoinNamingContext extends NamingContext
{
    public CoinNamingContext(Hashtable e, Name baseName)
        throws NamingException
    {
        super(e, baseName);
    }

    public CoinNamingContext(Hashtable e, Name baseName, NamingService service)
        throws NamingException
    {
        super(e, baseName, service);
    }

    public CoinNamingContext(Hashtable e, String baseName, NamingService service)
        throws NamingException
    {
        super(e, baseName, service);
    }

    /**
     * 重载命名解析器
     *
     * @return 命名解析器
     */
    protected NameParser createParser()
    {
        return new CoinNamingParser();
    }
}
