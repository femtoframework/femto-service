package org.femtoframework.service.apsis.rmi;

import org.femtoframework.bean.BeanPhase;
import org.femtoframework.coin.event.BeanEvent;
import org.femtoframework.coin.event.BeanEventListener;
import org.femtoframework.service.rmi.RmiUtil;
import org.femtoframework.util.StringUtil;

import javax.annotation.ManagedBean;
import java.rmi.Remote;

/**
 * 实现java.rmi.Remote的对象和拥有ManagedBean Annotation的对象的生命周期侦听者
 *
 * @author fengyun
 * @version 1.00 2005-6-11 11:20:21
 */
public class RemoteLifecycleListener implements BeanEventListener
{
    private void exportObject(Object obj, String uri)
    {
        if (!RmiUtil.isExported(obj)) {
            RmiUtil.exportObject(obj, uri);
        }
    }

    /**
     * Handle bean event
     *
     * @param event Bean Event
     */
    @Override
    public void handleEvent(BeanEvent event) {
        BeanPhase phase = event.getPhase();
        if (phase == BeanPhase.STARTED) {
            Object obj = event.getSource();
            String qualifiedName = event.getComponent().getQualifiedName();
            if (obj instanceof Remote) {
                exportObject(obj, qualifiedName);
            }
            else {
                Class<?> clazz = obj.getClass();
                ManagedBean bo = clazz.getAnnotation(ManagedBean.class);
                if (bo != null) {
                    String uri = bo.value();
                    if (StringUtil.isValid(uri)) {
                        exportObject(obj, uri);
                    }
                    else {
                        exportObject(obj, qualifiedName);
                    }
                }
            }
        }
    }
}
