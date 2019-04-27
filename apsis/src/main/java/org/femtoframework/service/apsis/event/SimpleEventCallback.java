package org.femtoframework.service.apsis.event;

import org.femtoframework.service.event.EventCallback;
import org.femtoframework.service.event.EventCallbackable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * SimpleEventCallback
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-12-20 17:49:23
 */
public class SimpleEventCallback<E> implements EventCallback, Externalizable
{
    private Throwable throwable;
    private Object source;

    public SimpleEventCallback() {
    }

    public SimpleEventCallback(Object source)
    {
        this.source = source;
    }


    public void setSource(Object source)
    {
        this.source = source;
    }

    /**
     * 是否执行失败？如果有异常则认为失败
     *
     * @return
     */
    public boolean isException()
    {
        return throwable != null;
    }

    /**
     * 得到失败源，也就是失败的原因
     *
     * @return
     */
    public Throwable getException()
    {
        return throwable;
    }

    /**
     * 设置异常
     *
     * @param t
     */
    public void setException(Throwable t)
    {
        throwable = t;
    }

    /**
     * 获取事件源
     *
     * @return
     */
    public Object getSource()
    {
        return source;
    }

    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final SimpleEventCallback that = (SimpleEventCallback) o;

        return !(source != null ? !source.equals(that.source) : that.source != null);

    }

    public int hashCode()
    {
        int result;
        result = (throwable != null ? throwable.hashCode() : 0);
        result = 29 * result + (source != null ? source.hashCode() : 0);
        return result;
    }


    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("SimpleEventCallback");
        buf.append("{throwable=").append(throwable);
        buf.append(",source=").append(source);
        buf.append('}');
        return buf.toString();
    }

    @Override
    public void writeExternal(ObjectOutput oos) throws IOException {
        oos.writeObject( throwable);
        if (source instanceof EventCallbackable) { //Don't have to send the callbackHandler back
            ((EventCallbackable)source)._setCallbackHandler(null);
        }
        oos.writeObject( source);
    }

    @Override
    public void readExternal(ObjectInput ois) throws IOException, ClassNotFoundException {
        throwable = (Throwable) ois.readObject();
        source = ois.readObject();
    }
}
