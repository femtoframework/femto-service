package org.femtoframework.service.rmi;

import java.io.Serializable;

/**
 * RemoteStub的超类，它用来标识对象可以被直接传递到客户端而不需要动态导出
 */
public interface RemoteStub extends Serializable
{
}
