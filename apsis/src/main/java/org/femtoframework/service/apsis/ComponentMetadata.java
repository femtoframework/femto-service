package org.femtoframework.service.apsis;

import org.femtoframework.bean.NamedBean;
import org.femtoframework.bean.info.BeanInfo;

/**
 * Model 的Metadata信息
 *
 * @author fengyun
 * @version 1.00 2005-11-16 12:34:13
 */
public interface ComponentMetadata extends NamedBean
{
    /**
     * 组件对应的类名，如果是Pipeline，那么对应Pipeline相应的类
     */
    String getClassName();

    /**
     * 返回BeanInfo信息，如果是Pipeline，那么对应Pipeline相应的BeanInfo
     *
     * @return BeanInfo信息
     */
    BeanInfo getBeanInfo();

}
