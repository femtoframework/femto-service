package org.femtoframework.service.apsis.ext;

import org.femtoframework.bean.BeanPhase;
import org.femtoframework.bean.annotation.Description;
import org.femtoframework.bean.annotation.Property;
import org.femtoframework.naming.NamingServiceFactory;
import org.femtoframework.naming.NamingUtil;
import org.femtoframework.net.message.MessageListener;
import org.femtoframework.net.message.MessageMetadata;
import org.femtoframework.net.message.MessageRegistry;
import org.femtoframework.net.message.MessageRegistryUtil;
import org.femtoframework.service.*;
import org.femtoframework.service.apsis.ApsisConstants;
import org.femtoframework.service.apsis.ApsisServer;
import org.femtoframework.service.client.ClientUtil;
import org.femtoframework.util.queue.LinkedQueue;
import org.femtoframework.util.thread.ExecutorUtil;
import org.femtoframework.util.thread.LifecycleThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ManagedBean;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * 简单ApsisServer
 *
 * @author fengyun
 * @version 1.00 2005-5-22 16:21:13
 */
@ManagedBean
public class SimpleApsisServer extends LifecycleThread
        implements ApsisServer, MessageListener, Runnable {
    /**
     * 日志
     */
    private static Logger log = LoggerFactory.getLogger("apsis/server");


    public static final int DEFAULT_MAX_WAITING_COUNT = 16 * 1024;

    /**
     * 服务器
     */
    private Map<String, Server> servers = new HashMap<String, Server>();

    /**
     * 连接器
     */
    private Map<String, Connector> connectors = new HashMap<String, Connector>();

    /**
     * 执行器
     */
    private ExecutorService executor = null;

    /**
     * 执行队列
     */
    private final Queue<MessageCart> queue = new LinkedQueue<>();

    /**
     * 所有服务器都能够收到的消息侦听者
     */
    private MessageListener allServerListener = null;

    /**
     * 最大等待消息
     */
    @Description("Max count of the waited message in queue")
    private int maxWaitingCount = DEFAULT_MAX_WAITING_COUNT;

    private long id;

    private String type;

    /**
     * 添加服务器，服务器在所有容器之后初始化，所以这个方法的位置不能在AddContainer之前
     *
     * @param server 服务器
     */
    public void addServer(Server server) {
        servers.put(server.getName(), server);
    }

    /**
     * 删除服务器
     *
     * @param name 服务器名称
     */
    public Server removeServer(String name) {
        return servers.remove(name);
    }

    /**
     * 根据服务器名称返回服务器
     *
     * @param name 服务器名称
     */
    public Server getServer(String name) {
        return servers.get(name);
    }

    /**
     * 返回所有服务器名称
     *
     * @return 所有服务器名称
     */
    @Property (writable = false)
    @Description("Thread pool")
    public Collection<String> getServerNames() {
        return servers.keySet();
    }

    /**
     * 设置线程执行器
     *
     * @param executor 线程池
     */
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * 返回线程执行器
     *
     * @return 线程执行器
     */
    @Property (writable = false)
    @Description("Names of the all containers")
    public ExecutorService getExecutor() {
        return executor;
    }

    /**
     * 添加连接器
     *
     * @param connector 连接器
     */
    public void addConnector(Connector connector) {
        //注入当前服务器
        if (connector.getServer() == null) {
            connector.setServer(this);
        }
        connectors.put(connector.getName(), connector);
    }

    /**
     * 返回连接器
     *
     * @param name 连接器名称
     */
//    @Function (desc = "Query connector")
    public Connector getConnector(String name) {
        return connectors.get(name);
    }

    /**
     * 根据连接器名称删除连接器
     *
     * @param name 连接器名称
     */
    public Connector removeConnector(String name) {
        return connectors.remove(name);
    }

    /**
     * 返回所有连接器名称
     *
     * @return 所有连接器名称
     */
    @Property (writable = false)
    @Description("Names of the all connectors")
    public Collection<String> getConnectorNames() {
        return connectors.keySet();
    }

    /**
     * 返回服务器应用类型
     *
     * @return 服务器应用类型
     */
    public String getType() {
        return type;
    }

    /**
     * 返回服务器标识
     *
     * @return 服务器标识
     */
    public long getId() {
        return id;
    }


    /**
     * 消息注册
     */
    private MessageRegistry registry = MessageRegistryUtil.getRegistry();

    /**
     * 处理消息
     *
     * @param message 消息
     */
    public void process(Object message) {
        MessageMetadata metadata = registry.getMetadata(message);
        if (metadata == null) {
            //No such message type;
            if (log.isWarnEnabled()) {
                log.warn("Can't process the message:" + message + ", No message metadata.");
                return;
            }
        }
        onMessage(metadata, message);
    }

    /**
     * 初始化
     */
    public void _doInit() {
        super._doInit();

        id = ServerID.getLocal().getId();
        type = System.getProperty("cube.system.type");

        //Join
        Iterator it = connectors.keySet().iterator();
        String name;
        while (it.hasNext()) {
            name = (String)it.next();
            Connector connector = connectors.get(name);
            connector.setServer(this);
        }

        //如果没有配置executor 那么我们将创建一个
        if (executor == null) {
            executor = ExecutorUtil.newThreadPool("apsis_thread_pool", 10, 50, 250);
        }
        if (log.isInfoEnabled()) {
            log.info("Apsis Server inited! ");
        }
    }

    /**
     * 实现启动
     *
     */
    public void _doStart() {
        super._doStart();


        //侦听所有的客户端改变信息
        ClientUtil.getManager();

        //默认启动 apsis:// 名字服务器
        NamingServiceFactory factory = NamingUtil.getServiceFactory(ApsisConstants.SCHEME);
        factory.getLocalService();
    }

    /**
     * 当消息到达的时候调用
     *
     * @param metadata 消息元数据
     * @param message  消息
     */
    public void onMessage(MessageMetadata metadata, Object message) {
        if (!getBeanPhase().isBefore(BeanPhase.STARTED)) {
            log.debug("Apsis server is not inited");
            return;
        }

        if (metadata == null) {
            throw new IllegalArgumentException("No metadata of the message:" + message);
        }
        String listenerName = metadata.getListener();
        MessageListener listener;
        if (MessageMetadata.DEFAULT_LISTENER.equals(listenerName)) {
            listener = allServerListener;
        }
        else {
            Server server = getServer(listenerName);
            if (server == null) {
                if (log.isWarnEnabled()) {
                    log.warn("No such server deployed:" + listenerName);
                }
                listener = ServerErrorListener.getInstance();
            }
            else if (!(server instanceof MessageListener)) {
                if (log.isWarnEnabled()) {
                    log.warn("The server can't listen message");
                }
                listener = ServerErrorListener.getInstance();
            }
            else {
                listener = (MessageListener)server;
            }
        }

        //进入执行队列
        int size = queue.size();
        if (size < maxWaitingCount) {
            queue.offer(new MessageCart(metadata, message, listener));
        }
        else {
            if (log.isErrorEnabled()) {
                log.error("Too many messages in queue:" + size + ", try to remove 10 messages");
            }
            try {
                //删除10个任务，然后重新进入
                synchronized (queue) {
                    for (int i = 0; i < 10; i++) {
                        queue.remove(i);
                    }
                    queue.offer(new MessageCart(metadata, message, listener));
                }
            }
            catch (Throwable t) {
                log.error("Error", t);
            }
            tryToCheckRunning = true;
            //发送线程中断信号
            getThread().interrupt();
        }
    }

    /**
     * 正在执行的消息，可以在这层实现取消任务等
     */
    private final Map<Integer, MessageCart> runningMessages = new HashMap<Integer, MessageCart>();

    /**
     * 是否尝试进行执行任务的检查
     */
    private boolean tryToCheckRunning = false;

    /**
     * 实际要执行的任务方法。<br>
     * 通过这个方法，来执行实际的程序<br>
     * 如果出现异常，ErrorHandler的错误处理返回<code>true</code>，<br>
     * 那么该循环线程就终止循环。
     *
     * @throws Exception 各类执行异常
     * @see #run()
     */
    protected void doRun() throws Exception {
        MessageCart cart = queue.poll();
        if (cart.isTimeout()) {
            //如果消息已经超时，那么不进行处理
            return;
        }

        cart.addToRunning(runningMessages);
        //提交任务
        cart.future = executor.submit(cart);

        //当前线程被中断
        if (Thread.currentThread().isInterrupted()) {
            //如果需要参数正在执行的消息，那么对消息进行检查
            if (tryToCheckRunning) {
                try {
                    checkRunning();
                }
                catch (Throwable t) {
                    log.error("Error", t);
                }
                tryToCheckRunning = false;
            }
            //清除中断状态
            Thread.interrupted();
        }
    }

    /**
     * 检查那些正在执行的消息，分别中断那些运行时间过长的
     */
    private void checkRunning() {
        synchronized (this) {
            List<MessageCart> removed = null;
            for (int id : runningMessages.keySet()) {
                MessageCart mc = runningMessages.get(id);
                synchronized (mc) {
                    if (mc.isTimeout()) {
                        mc.cancel();
                        if (removed == null) {
                            removed = new ArrayList<>();
                        }
                        removed.add(mc);
                    }
                }
            }
            if (removed != null) {
                for (MessageCart messageCart : removed) {
                    runningMessages.remove(messageCart.getId());
                }
            }
        }
    }

    /**
     * 返回最大等待的消息总数
     *
     * @return 最大等待的消息总数
     */
    public int getMaxWaitingCount() {
        return maxWaitingCount;
    }

    /**
     * 设置最大的等待消息总数
     *
     * @param maxWaitingCount 最大等待消息总数
     */
    public void setMaxWaitingCount(int maxWaitingCount) {
        this.maxWaitingCount = maxWaitingCount;
    }
}
