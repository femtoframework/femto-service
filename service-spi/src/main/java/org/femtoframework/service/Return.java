package org.femtoframework.service;

import org.femtoframework.parameters.Parameters;

/**
 * 一次调用的返回
 *
 * @author fengyun
 * @version 1.00 Apr 27, 2002 6:52:49 PM
 */
public interface Return
    extends FeedBack, Parameters
{
    /**
     * 返回Session标识
     *
     * @return [String] Session标识
     */
    SessionID getSessionID();

    /**
     * 返回异常
     *
     * @return 异常
     */
    Throwable getThrowable();
}
