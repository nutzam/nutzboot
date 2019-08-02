package org.nutz.boot.starter.thrift.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;
import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.MonitorObject;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(create = "init")
public class ThriftServerStarter implements ServerFace, MonitorObject {

    private static final Log log = Logs.get();

    protected static final String PRE = "thrift";

    @PropDoc(defaultValue = "default", value = "TServer名称,可以多个TServer配置,名称用逗号分割")
    public static final String PROP_SERVERS = PRE + ".servers";

    @PropDoc(value = "TServer类型", defaultValue = "threadpool", possible = {"threadpool", "simple", "threadedSelector", "nonblocking"})
    public static final String PROP_SERVER_TYPE = ".server.${name}.type";

    @PropDoc(value = "TServer监听的端口", defaultValue = "9090")
    public static final String PROP_SERVER_PORT = ".server.${name}.port";

    @PropDoc(value = "TServerTransport类型", defaultValue = "socket", possible = {"socket"})
    public static final String PROP_SERVER_TRANSPORT = ".server.${name}.transport";

    @PropDoc(value = "TProtocol类型", defaultValue = "binary", possible = {"binary", "compact", "json", "disable"})
    public static final String PROP_SERVER_DEFAULT_TYPE = ".server.${name}.protocol";

    @Inject
    protected PropertiesProxy conf;
    @Inject
    protected AppContext appContext;
    protected List<TServer> servers = new ArrayList<>();

    protected NutMap monitorProps = new NutMap();

    protected TProcessor getTProcessor() {
        List<TProcessor> list = appContext.getBeans(TProcessor.class);
        if (list.isEmpty()) {
            return null;
        }
        TMultiplexedProcessor multi = new TMultiplexedProcessor();
        for (TProcessor processor : list) {
            String name = processor.getClass().getEnclosingClass().getSimpleName();
            log.debugf("add processor/service name=%s", name);
            multi.registerProcessor(name, processor);
        }
        multi.registerDefault(list.get(0));
        updateMonitorValue(PRE + ".processor_count", list.size());
        return multi;
    }

    /**
     * 选用不同的服务器实现类
     */
    protected TServer getTServer(String prefix, TProcessor processor) throws TTransportException {
        TProtocolFactory protocolFactory = getTProtocol(prefix);
        String type = conf.get(prefix + ".type", "threadpool");
        updateMonitorValue(prefix + ".type", type);
        switch (type) {
        case "threadpool": {
            TThreadPoolServer.Args args = new TThreadPoolServer.Args(getTServerTransport(prefix, false));
            args.processor(processor);
            args.protocolFactory(protocolFactory);
            args.transportFactory(new TTransportFactory());
            return new TThreadPoolServer(args);
        }
        case "simple": {
            TSimpleServer.Args args = new TSimpleServer.Args(getTServerTransport(prefix, false));
            args.processor(processor);
            args.protocolFactory(protocolFactory);
            args.transportFactory(new TTransportFactory());
            return new TSimpleServer(args);
        }
        case "threadedSelector": {
            TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args((TNonblockingServerTransport) getTServerTransport(prefix, true));
            args.processor(processor);
            args.protocolFactory(protocolFactory);
            args.transportFactory(new TFramedTransport.Factory());
            return new TThreadedSelectorServer(args);
        }
        case "nonblocking": {
            TNonblockingServer.Args args = new TNonblockingServer.Args((TNonblockingServerTransport) getTServerTransport(prefix, true));
            args.processor(processor);
            args.protocolFactory(protocolFactory);
            args.transportFactory(new TFramedTransport.Factory());
            return new TNonblockingServer(args);
        }
        case "servlet": {
            log.info("using servlet init, nop");
            return null;
        }
        default:
            break;
        }
        throw Lang.noImplement();
    }

    protected TServerTransport getTServerTransport(String prefix, boolean nonblock) throws TTransportException {
        String transport = conf.get(prefix + ".transport", "socket");
        switch (transport) {
        case "socket": {
            int port = conf.getInt(prefix + ".port", 9090);
            int clientTimeout = conf.getInt(prefix + ".clientTimeout", 0);
            updateMonitorValue(prefix + ".port", port);
            updateMonitorValue(prefix + ".clientTimeout", clientTimeout);
            if (nonblock) {
                return new TNonblockingServerSocket(port, clientTimeout);
            } else {
                return new TServerSocket(port, clientTimeout);
            }
        }
        default:
            break;
        }
        throw Lang.noImplement();
    }

    protected TProtocolFactory getTProtocol(String prefix) {
        String protocol = conf.get(prefix + ".protocol", "binary");
        switch (protocol) {
        case "binary":
            return new TBinaryProtocol.Factory(true, true);
        case "json":
            return new TJSONProtocol.Factory();
        case "compact":
            return new TCompactProtocol.Factory();
        case "disable":
            return null;
        default:
            break;
        }
        throw Lang.noImplement();
    }

    public void init() throws Exception {
        TProcessor processor = getTProcessor();
        if (processor == null) {
            log.warn("none TProcessor found, thrift.service will not start!!!!!");
            return;
        }
        for (String serverName : Strings.splitIgnoreBlank(conf.get(PROP_SERVERS, "default"))) {
            String prefix = PRE + ".server." + serverName;
            TServer server = getTServer(prefix, processor);
            if (server != null)
                servers.add(server);
        }
    }

    public void start() throws Exception {
        if (servers.isEmpty())
            return;
        for (TServer tServer : servers) {
            Thread t = new Thread("thrift.service." + tServer.getClass().getSimpleName() + "." + System.currentTimeMillis()) {
                public void run() {
                    tServer.serve();
                }
            };
            t.setDaemon(true);
            t.start();
        }
        if (log.isDebugEnabled())
            log.debug("Thrift monitor props:\r\n"+getMonitorForPrint());
    }

    public void stop() throws Exception {
        for (TServer tServer : servers) {
            tServer.stop();
        }
    }
    
    // 监控相关的方法
    
    public Collection<String> getMonitorKeys() {
        return monitorProps.keySet();
    }

    public Object getMonitorValue(String key) {
        return monitorProps.get(key);
    }
    
    public void updateMonitorValue(String key, Object value) {
        monitorProps.put(key, value);
    }

    public String getMonitorName() {
        return "thrift";
    }
}
