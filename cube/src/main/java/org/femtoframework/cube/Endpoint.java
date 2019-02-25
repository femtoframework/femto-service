package org.femtoframework.cube;

import org.femtoframework.bean.NamedBean;

/**
 * Endpoint
 * 
 * @author fengyun
 * @version 1.00 2011-09-09 20:50
 */
public interface Endpoint extends NamedBean {
    /**
     * 返回主机端口
     *
     * @return 主机端口
     */
    int getPort();

}
