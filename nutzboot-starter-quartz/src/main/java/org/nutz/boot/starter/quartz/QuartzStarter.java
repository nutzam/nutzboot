package org.nutz.boot.starter.quartz;

import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.integration.quartz.QuartzIocLoader;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class QuartzStarter implements IocLoaderProvider {

	public IocLoader getIocLoader() {
		return new QuartzIocLoader();
	}

    
}
