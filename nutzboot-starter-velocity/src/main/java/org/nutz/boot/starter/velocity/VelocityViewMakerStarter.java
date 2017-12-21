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

    private VelocityEngine engine;
    private String templateClasspath = "template";
    private String charsetEncoding = "UTF-8";

    public void init() {
        if (conf == null) {
            return;
        }
        log.debug("velocity init ....");
        engine = new VelocityEngine();
        if (conf.has("velocity.path")) {
            templateClasspath = conf.get("velocity.path");
        }
        if (conf.has("velocity.encoding")) {
            charsetEncoding = conf.get("velocity.encoding");
        }
        for (String key : conf.keySet()) {
            if (key.startsWith("velocity.")) {
                engine.setProperty(key.substring("velocity.".length()), conf.get(key));
            }
        }
        engine.setProperty("input.encoding", charsetEncoding);
        engine.setProperty("output.encoding", charsetEncoding);
        engine.setProperty("resource.loader", "classpath");
        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.Log4JLogChute");
        engine.setProperty("runtime.log.logsystem.log4j.category", "velocity");
        engine.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        engine.setProperty("velocimacro.library.autoreload", false);
        engine.setProperty("file.resource.loader.cache", true);
        engine.setProperty("directive.set.null.allowed", true);
        engine.init();
        log.debug("velocity init complete");
    }

    public View make(Ioc ioc, String type, final String value) {
        if ("vm".equals(type)) {
            return new VelocityView(value, engine, templateClasspath, charsetEncoding);
        }
        return null;
    }
}
