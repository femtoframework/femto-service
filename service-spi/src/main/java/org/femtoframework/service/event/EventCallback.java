package org.femtoframework.service.event;

/**
 * EventCallback
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-12-20 17:43:49
 */
public interface EventCallback
{
    /**
     * 是否执行失败？如果有异常则认为失败
     *
     * @return 是否执行失败？如果有异常则认为失败
     */
    public boolean isException();

    /**
     * 得到失败源，也就是失败的原因
     *
     * @return 得到失败源
     */
    public Throwable getException();

    /**
     * 设置异常
     *
     * @param t
     */
    public void setException(Throwable t);

    /**
     * 获取事件源
     *
     * @return 获取事件源
     */
    public Object getSource();

    /**
     * 设置源事件
     *
     * @param source 源
     */
    public void setSource(Object source);
}
