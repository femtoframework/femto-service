package org.femtoframework.service.event;

import java.util.List;

/**
 * 批处理数据源
 *
 * @author fengyun
 * @version 1.00 2006-12-7 14:36:13
 */
public interface BatchDataSource<E>
{
    /**
     * 返回实体列表
     *
     * @return 实体列表
     */
    List<E> getEntities() throws Exception;
}
