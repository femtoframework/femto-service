package org.femtoframework.service.apsis.marshal;

import org.femtoframework.io.FastObjectInputStream;
import org.femtoframework.io.ObjectCodec;
import org.femtoframework.io.SwitchInputStream;
import org.femtoframework.io.SwitchOutputStream;

import java.io.*;
import java.lang.ref.SoftReference;

/**
 * 串行化工具，它能够将Remote对象自动导出
 *
 * @author fengyun
 * @version Dec 26, 2002 12:30:13 PM
 */

public class ApsisMarshaller implements ObjectCodec {
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;
    private SwitchInputStream sis = null;
    private SwitchOutputStream sos = null;


    private void initInput(InputStream in)
        throws IOException {
        if (sis == null || ois == null) {
            sis = new SwitchInputStream(in);
        }
        else {
            sis.setInput(in);
        }

        if (ois == null) {
            ois = new FastObjectInputStream(sis);
        }
    }

    private void initOutput(OutputStream out)
        throws IOException {
        if (sos == null || oos == null) {
            sos = new SwitchOutputStream(out);
        }
        else {
            sos.setOutput(out);
        }

        if (oos == null) {
            oos = new ObjectMarshalStream(sos);
        }
    }


    public void writeObject(OutputStream out, Object obj)
        throws IOException {
        initOutput(out);
        try {
            oos.reset();
            oos.writeObject(obj);
            oos.flush();
        }
        catch (IOException ioe) {
            oos = null;
            throw ioe;
        }
        catch (Throwable t) {
            oos = null;
            throw new IOException("Error:" + t.getMessage());
        }
        finally {
            sos.clearOutput();
        }
    }

    /**
     * 读取对象
     *
     * @param in
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Object readObject(InputStream in)
        throws IOException, ClassNotFoundException {
        initInput(in);

        try {
            return ois.readObject();
        }
        catch (IOException ioe) {
            ois = null;
            throw ioe;
        }
        catch (ClassNotFoundException cnfe) {
            ois = null;
            throw cnfe;
        }
        catch (Throwable t) {
            ois = null;
            throw new IOException("Error:" + t.getMessage());
        }
        finally {
            sis.clearInput();
        }
    }

    /**
     * 根据输入流创建对象输入流
     *
     * @param input 输入流
     */
    public ObjectInputStream getObjectInput(InputStream input)
        throws IOException {
        return new FastObjectInputStream(input);
    }

    /**
     * 根据输出流创建对象输出流
     *
     * @param output 输出流
     */
    public ObjectOutputStream getObjectOutput(OutputStream output)
        throws IOException {
        return new ObjectMarshalStream(output);
    }


    private static ThreadLocal<SoftReference<ApsisMarshaller>> local = new ThreadLocal<>();

    public static ApsisMarshaller getInstance() {
        SoftReference<ApsisMarshaller> sr = local.get();
        ApsisMarshaller mh = null;
        if (sr != null) {
            mh = sr.get();
        }
        if (mh == null) {
            mh = new ApsisMarshaller();
            sr = new SoftReference<>(mh);
            local.set(sr);
        }
        return mh;
    }
}
