package org.femtoframework.service.apsis.ext;

import org.femtoframework.net.message.MessageListener;
import org.femtoframework.net.message.MessageMetadata;
import org.femtoframework.net.message.RequestMessage;
import org.femtoframework.net.message.RequestResponse;
import org.femtoframework.service.Event;
import org.femtoframework.service.Response;
import org.femtoframework.service.SimpleFeedBack;
import org.femtoframework.service.event.GenericEvent;
import org.femtoframework.service.apsis.rmi.RmiRequest;
import org.femtoframework.service.apsis.rmi.RmiResponse;
import org.femtoframework.util.status.StatusException;

/**
 * ServerErrorListener
 *
 * @author <a href="mailto:renex@bolango.cn">rEneX</a>
 * @version 1.00 2005-12-14 14:02:15
 */
class ServerErrorListener implements MessageListener
{

    /* 实例定义 */
    private static ServerErrorListener instance = null;

    /**
     * 获取单例方法实现
     *
     * @return 单例
     */
    public static ServerErrorListener getInstance()
    {
        if (instance == null) {
            instance = new ServerErrorListener();
        }
        return instance;
    }

    private ServerErrorListener()
    {
    }

    /**
     * 当消息到达的时候调用
     *
     * @param metadata 消息元数据
     * @param message  消息
     */
    public void onMessage(MessageMetadata metadata, Object message)
    {
        if (message instanceof RequestResponse) {
            RequestResponse task = (RequestResponse) message;
            RequestMessage req = (RequestMessage) task.getRequest();

            //只有以下两种消息
//            if (req instanceof CommandRequest) {
//                Object rep = task.getResponse();
//                ((CommandResponse) rep).setCode(Response.SC_INVALID_REQUEST_TYPE);
//                task.ack();
//            }
//            else if (req instanceof BatchCommandRequest) {
//                Object rep = task.getResponse();
//                ((BatchCommandResponse) rep).setCode(Response.SC_INVALID_REQUEST_TYPE);
//                task.ack();
//            }
//            else
                if (req instanceof RmiRequest) {
                Object rep = task.getResponse();
                ((RmiResponse) rep).setException(true);
                ((RmiResponse) rep).setResult(new StatusException(Response.SC_INVALID_REQUEST_TYPE));
                task.ack();
            }
            else if (req instanceof GenericEvent || req instanceof Event) {
                return;
            }
            Object rep = task.getResponse();
            if (rep instanceof SimpleFeedBack) {
                ((SimpleFeedBack)rep).setCode(Response.SC_INVALID_REQUEST_TYPE);
                task.ack();
            }
        }
    }
}
