package org.nutz.boot.starter.thrift.server;

import javax.servlet.Servlet;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServlet;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class ThriftWebServletFace implements WebServletFace {
    
    @Inject
    protected ThriftServerStarter thriftServerStarter;
    
    @Inject
    protected PropertiesProxy conf;
    
    protected TServlet tServlet;
    
    public void setThriftServiceStarter(ThriftServerStarter thriftServerStarter) {
        this.thriftServerStarter = thriftServerStarter;
        TProtocolFactory factory = thriftServerStarter.getTProtocol(conf.get(ThriftServerStarter.PRE + "server.servlet.protocol", "binary"));
        TProcessor processor = thriftServerStarter.getTProcessor();
        if (processor != null && factory != null)
            tServlet = new TServlet(processor, factory);
    }

    public String getName() {
        return "thrify";
    }

    public String getPathSpec() {
        return conf.get("thrift.server.servlet.pathspec", "/thrify");
    }

    public Servlet getServlet() {
        return tServlet;
    }

}
