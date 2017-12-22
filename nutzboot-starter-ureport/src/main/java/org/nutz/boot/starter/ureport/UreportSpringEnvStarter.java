package org.nutz.boot.starter.ureport;

import org.nutz.boot.tools.SpringWebContextProxy;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class UreportSpringEnvStarter extends SpringWebContextProxy {

    public UreportSpringEnvStarter() {
        configLocation = "classpath:ureport-spring-context.xml";
        selfName = "ureport";
    }

}
