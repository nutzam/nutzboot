package io.nutz.demo.custom.starter;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean
public class MySimpleServerStarter implements ServerFace {

    private static final Log log = Logs.get();

    public static String PRE = "mystarter.";

    @PropDoc(value = "欢迎语")
    public static String PROP_WELCOME = PRE + "welcome";

    @Inject
    protected PropertiesProxy conf;

    @Override
    public void start() throws Exception {
        log.infof("welcome, %s!", conf.get(PROP_WELCOME, "wendal"));
    }
}
