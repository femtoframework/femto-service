package org.femtoframework.service;


import org.femtoframework.util.status.Status;

/**
 * 该接口表示一个信息发送后的反馈结果。
 *
 * @author yyli@netdao.com
 * @author fengyun
 * @version 1.01 2002年5月27日
 */
public interface FeedBack extends Status
{
    /**
     * 状态成功
     */
    int SC_OK = 200;

    /**
     * 未知异常
     */
    int SC_UNEXPECTED = 501;

    /**
     * 是否正确执行<br>
     * 等价于 #getCode() == SC_OK
     *
     * @return [boolean] 状态是否正确
     */
    boolean isStatusOK();
}
