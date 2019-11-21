package org.femtoframework.service.apsis;

import org.femtoframework.coin.CoinUtil;
import javax.naming.NamingException;

/**
 * Apsis Server Util
 *
 * @author fengyun
 * @version 1.00 2005-9-19 20:39:56
 */
public class ApsisServerUtil {

    private static ApsisServer server;

    public static ApsisServer getServer() {
        if (server == null) {
            try {
                server = (ApsisServer)CoinUtil.getModule().getLookup().lookupBean("apsis:apsis_server");
            } catch (NamingException e) {
                throw new IllegalStateException("Apsis server is not initialized");
            }
        }
        return server;
    }
}