package org.femtoframework.service.event;

import java.util.EventListener;

/**
 * 桢听者装载桢听者
 *
 * @author fengyun
 * @version 1.00 2005-9-19 21:30:24
 */
public interface ListenerLoadListener extends EventListener
{
    /**
     * 当事件侦听者初始化的时候调用
     *
     * @param namespace   所在的名字空间
     * @param component   模块名称
     * @param listener    原始的组件
     * @param pipeline    事件处理管道
     */
    void onLoad(String namespace, String component, Object listener, EventPipeline pipeline);
}
