package org.femtoframework.service.apsis;

/**
 * 需要ApsisService的接口标识类
 *
 * @author fengyun
 * @version 1.00 2005-9-7 0:12:04
 */
public interface ApsisServiceAware
{
    /**
     * 设置ApsisService
     *
     * @param service ApsisNamespace
     */
    public void setService(ApsisNamespace service);
}
