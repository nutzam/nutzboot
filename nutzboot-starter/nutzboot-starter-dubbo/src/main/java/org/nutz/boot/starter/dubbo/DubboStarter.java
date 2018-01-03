package org.nutz.boot.starter.dubbo;

import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.boot.starter.ServerFace;
import org.nutz.integration.dubbo.DubboIocLoader;
import org.nutz.integration.dubbo.DubboManager;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

@IocBean
public class DubboStarter implements IocLoaderProvider, ServerFace {
    
	@Inject
	protected PropertiesProxy conf;
	
	@Inject("refer:$ioc")
	protected Ioc ioc;
	
    public IocLoader getIocLoader() {
    	DubboIocLoader iocLoader = new DubboIocLoader(Strings.splitIgnoreBlank(conf.get("dubbo.xmlPaths", "dubbo.xml")));
    	
    	return iocLoader;
    }

	@Override
	public void start() throws Exception {
		ioc.get(DubboManager.class);
	}

	@Override
	public void stop() throws Exception {
	}

	@Override
	public boolean isRunning() {
		return true;
	}

	@Override
	public boolean failsafe() {
		// TODO Auto-generated method stub
		return false;
	}
    
}
