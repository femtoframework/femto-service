package org.femtoframework.service.apsis.client;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * MultiResponse
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-11-30 16:30:54
 */
public class MultiResponse
    extends SimpleResponse {

    private Object responses[];

    public MultiResponse(Object[] responses) {
        this.responses = responses;
    }

    public Object[] getResponses() {
        return responses;
    }

    public void setResponses(Object[] responses) {
        this.responses = responses;
    }

    /**
     * 对象输入流
     *
     * @param ois 对象输入流
     */
    public void readExternal(ObjectInput ois) throws IOException, ClassNotFoundException {
        super.readExternal(ois);
        responses = (Object[])ois.readObject();
    }

    /**
     * 输出对象
     *
     * @param oos 对象输出流
     */
    public void writeExternal(ObjectOutput oos) throws IOException {
        super.writeExternal(oos);
        oos.writeObject(responses);
    }
}
