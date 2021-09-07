package org.nutz.boot.starter.dubbo;

import org.nutz.boot.AppContext;
import org.nutz.boot.aware.AppContextAware;
import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.integration.dubbo.DubboConfigIocLoader;
import org.nutz.integration.dubbo.DubboIocLoader;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class DubboIocLoaderStarter implements IocLoaderProvider, AppContextAware {

	private static final Log log = Logs.get();

	protected PropertiesProxy conf;

	protected Ioc ioc;
	
	protected AppContext appContext;

	public IocLoader getIocLoader() {
		if (conf.has("dubbo.xmlPaths")) {
			log.info("found dubbo.xmlPaths, use it");
			return new DubboIocLoader(Strings.splitIgnoreBlank(conf.get("dubbo.xmlPaths", "dubbo.xml")));
		}
		if (conf.getClass().getClassLoader().getResource("dubbo.xml") != null) {
			log.info("found dubbo.xml, use it");
			return new DubboIocLoader("dubbo.xml");
		}
		log.debug("using dubbo configure from PropertiesProxy");
		String basePackages = conf.get("dubbo.annotation.packages");
		if (Strings.isBlank(basePackages))
			basePackages = appContext.getPackage();
		conf.put("dubbo.scan.basePackages", basePackages);
		conf.putIfAbsent("dubbo.protocol.port", ""+appContext.getServerPort("dubbo.protocol.port", 8888));
		return new DubboConfigIocLoader(ioc, conf);
	}

	public void setAppContext(AppContext appContext) {
		conf = appContext.getConf();
		ioc = appContext.getIoc();
		this.appContext = appContext;
	}
}
