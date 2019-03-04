package org.femtoframework.service.apsis.rmi;

import org.femtoframework.bean.Nameable;
import org.femtoframework.coin.CoinUtil;
import org.femtoframework.coin.ResourceType;
import org.femtoframework.coin.naming.CoinNamingParser;
import org.femtoframework.net.message.MessageListener;
import org.femtoframework.net.message.MessageMetadata;
import org.femtoframework.net.message.RequestResponse;
import org.femtoframework.service.Server;
import org.femtoframework.service.rmi.ObjID;
import org.femtoframework.service.rmi.StrOID;
import org.femtoframework.service.rmi.server.RemoteCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Name;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;

/**
 * Rmi服务器
 *
 * @author fengyun
 * @version 1.00 2005-5-22 17:39:53
 */
public final class RmiServer
    implements Server, MessageListener, Nameable {

    private String name;

    private Logger log = LoggerFactory.getLogger("apsis/rmi_server");

    private CoinNamingParser namingParser = new CoinNamingParser();

    /**
     * 当消息到达的时候调用
     *
     * @param metadata 消息元数据
     * @param message  消息
     */
    public void onMessage(MessageMetadata metadata, Object message) {
        if (message instanceof RequestResponse) {
            RequestResponse task = (RequestResponse)message;
            RmiRequest req = (RmiRequest)task.getRequest();
            RmiResponse rep = (RmiResponse)task.getResponse();

            ObjID id = req.getObjID();

            Object[] args = req.getArguments();
            long opnum = req.getMethod();
            if (log.isTraceEnabled()) {
                log.trace("[RMI]Call:objID=" + id + ";opnum=" + opnum);
            }

            try {
                try {
                    Object result = RemoteCall.invoke(id, args, opnum);
                    rep.setException(false);
                    rep.setResult(result);
                } catch (NoSuchObjectException nsoe) {
                    if (id instanceof StrOID) {
                        String uri = ((StrOID) id).getUri();
                        Name name = namingParser.parse(uri);
                        //TODO
                        Object obj = CoinUtil.getModule().getLookup().lookup(ResourceType.BEAN, name);
                        if (obj != null) {
                            //再次调用
                            Object result = RemoteCall.invoke(id, args, opnum);
                            rep.setException(false);
                            rep.setResult(result);
                        } else {
                            log.info("No such object:" + uri + ".");
                            throw nsoe;
                        }
                    }
                    else {
                        log.info("No such object:" + name + nsoe.getMessage() + " export now.");
                        throw nsoe;
                    }
                }
            }
            catch (NoSuchObjectException re) {
                if (log.isDebugEnabled()) {
                    log.debug(re.getMessage());
                }
                rep.setException(true);
                rep.setResult(re);
            }
            catch (RemoteException re) {
                if (log.isDebugEnabled()) {
                    log.debug("RemoteException", re);
                }
                rep.setException(true);
                rep.setResult(re);
            }
            catch (Throwable re) {
                if (log.isDebugEnabled()) {
                    log.debug("Error", re);
                }
                rep.setException(true);
                rep.setResult(re);
            }
            finally {
                task.ack();
            }
        }
        else {
            if (log.isWarnEnabled()) {
                log.warn("Can't handle message:" + message);
            }
        }
    }


    @Override
    public final String toString() {
        return "RmiServer";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
