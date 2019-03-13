package org.femtoframework.service.apsis.session;

import org.femtoframework.service.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * SessionDumper
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-8-1 14:10:46
 */
public abstract class SessionDumper
    implements Runnable
{
    private SessionContainer container;
    protected Logger log = LoggerFactory.getLogger("apsis/session/dumper");

    public void setContainer(SessionContainer container)
    {
        this.container = container;
    }

    public SessionContainer getContainer()
    {
        return container;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run()
    {
        log.info("Start to dumping..........");

        synchronized (container) {
            dump(container.getSessions());
        }

        log.info("Session pools dumped.");
    }

    protected abstract void dump(Collection sessions);

    /**
     * 重新装载Session
     *
     * @return Collection&lt;Session&gt;
     */
    public abstract <S extends Session> Collection<S> load();

}
