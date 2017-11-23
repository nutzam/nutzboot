package org.nutz.boot.starter.jetx;

import java.util.Properties;

import org.nutz.boot.AppContext;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

import jetbrick.template.JetEngine;
import jetbrick.template.web.JetWebEngine;
import jetbrick.template.web.nutz.JetTemplateView;

@IocBean(name="$views_jetx")
public class JetxViewMakerStarter implements ViewMaker {
	
	@Inject
	protected PropertiesProxy conf;
	
	@Inject
	protected AppContext appContext;

	protected String suffix;

	protected JetEngine engine;

    public View make(Ioc ioc, String type, final String value) {
    	if (engine == null) {
    		Properties prop = new Properties();
    		for (String key : conf.keySet()) {
    			if (key.startsWith("jetx.")) {
    				prop.put(key.substring("jetx.".length()), conf.get(key));
    			}
    		}
    		prop.setProperty("jetx.template.loaders", "$classpathLoader");
    		prop.setProperty("$classpathLoader", "jetbrick.template.loader.ClasspathResourceLoader");
    		prop.setProperty("$classpathLoader.root", "/template/");
    		prop.setProperty("$classpathLoader.reloadable", "true");
    		engine = JetWebEngine.create(Mvcs.getServletContext(), prop, null);
            suffix = engine.getConfig().getTemplateSuffix().substring(1);
    	}
        if (suffix.equals(type)) {
            return new JetTemplateView(value);
        }
        return null;
    }
}
