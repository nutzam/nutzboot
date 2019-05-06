package org.nutz.boot.starter.freemarker;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

@IocBean(name="$views_freekmarker")
public class FreemarkerViewMaker implements ViewMaker {

    protected FreeMarkerConfigurer freeMarkerConfigurer;
    
    protected String iocName = "freeMarkerConfigurer";

	protected static final String PRE = "freemarker.";

	@PropDoc(group = "freemarker", value = "文件后缀",defaultValue = ".html")
	public static final String PROP_SUFFIX = PRE + "suffix";


	@Inject
	PropertiesProxy conf;


	public View make(Ioc ioc, String type, String value) {
		if ("fm".equalsIgnoreCase(type) || "ftl".equalsIgnoreCase(type)) {
		    if (freeMarkerConfigurer == null) {
		        for (String name : ioc.getNames()) {
                    if (iocName.equals(name)) {
                        freeMarkerConfigurer = ioc.get(FreeMarkerConfigurer.class);
                        break;
                    }
                }
		        if (freeMarkerConfigurer == null) {
		            freeMarkerConfigurer = new FreeMarkerConfigurer();
		            freeMarkerConfigurer.init();
		        }
		    }
			return new FreemarkerView(freeMarkerConfigurer, value);
		}
		return null;
	}

}
