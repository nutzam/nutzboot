package org.nutz.boot.starter.thymeleaf;

import org.nutz.boot.AppContext;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

@IocBean(name="$views_thymeleaf", create="init")
public class ThymeleafViewMakerStarter implements ViewMaker {

    private static final Log log = Logs.get();

    @Inject
    protected PropertiesProxy conf;

    @Inject
    protected AppContext appContext;

    protected NutMap prop = NutMap.NEW();

    public void init() {
        if (conf == null) {
            return;
        }
        log.debug("thymeleaf init ....");
        for (String key : conf.keySet()) {
            if (key.startsWith("thymeleaf.")) {
                prop.put(key.substring("thymeleaf.".length()), conf.get(key));
            }
        }
        log.debug("thymeleaf init complete");
    }

    @Override
    public View make(Ioc ioc, String type, String value) {
        if ("th".equalsIgnoreCase(type)) {
            return new ThymeleafView(appContext.getClassLoader(), prop, value);
        } else {
            return null;
        }
    }
}
