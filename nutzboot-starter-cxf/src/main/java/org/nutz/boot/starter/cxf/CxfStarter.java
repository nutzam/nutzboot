package org.nutz.boot.starter.cxf;

import javax.jws.WebService;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;

import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.nutz.boot.AppContext;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;

@IocBean
public class CxfStarter extends CXFNonSpringServlet implements WebServletFace {

    private static final long serialVersionUID = 6375628992908998229L;

    private static final Log log = Logs.get();

    @Inject
    protected PropertiesProxy conf;

    @Inject
    protected AppContext appContext;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    public String getName() {
        return "cxf";
    }

    public String getPathSpec() {
        return "/webservice/*";
    }

    public Servlet getServlet() {
        return this;
    }

    protected void loadBus(ServletConfig sc) {
        super.loadBus(sc);
        for (Class<?> klass : Scans.me().scanPackage(appContext.getPackage(), null)) {
            // 有@WebService和@IocBean注解的非接口类
            WebService ws = klass.getAnnotation(WebService.class);
            if (ws == null || klass.isInterface())
                continue;
            if (Strings.isBlank(ws.serviceName())) {
                log.infof("%s has @WebService but serviceName is blank, ignore", klass.getName());
                continue;
            }
            log.debugf("add WebService addr=/%s type=%s", ws.serviceName(), klass.getName());
            JaxWsServerFactoryBean sfb = new JaxWsServerFactoryBean();
            sfb.setServiceBean(ioc.get(klass));
            sfb.create();
        }
    }
}
