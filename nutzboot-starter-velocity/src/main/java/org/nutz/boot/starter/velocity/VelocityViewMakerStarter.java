package org.nutz.boot.starter.velocity;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.nutz.boot.AppContext;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;


@IocBean(name = "$views_velocity", create = "init")
public class VelocityViewMakerStarter implements ViewMaker {

    private static final Log log = Logs.get();

    @Inject
    protected PropertiesProxy conf;

    @Inject
    protected AppContext appContext;

    private VelocityEngine engine ;

    public void init() {
        if (conf == null) {
            return;
        }
        log.debug("velocity init ....");
        engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,"org.apache.velocity.runtime.log.Log4JLogChute");
        engine.setProperty("runtime.log.logsystem.log4j.category", "velocity");
        engine.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        for (String key : conf.keySet()) {
            if (key.startsWith("velocity.")) {
                engine.setProperty(key.substring("velocity.".length()), conf.get(key));
            }
        }
        engine.init();
        log.debug("velocity init complete");
    }

    public View make(Ioc ioc, String type, final String value) {
        if ("vm".equals(type)) {
            return new VelocityView(value,engine);
        }
        return null;
    }
}
