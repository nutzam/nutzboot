package org.nutz.boot.starter.dubbo;

import org.nutz.boot.starter.ServerFace;
import org.nutz.integration.dubbo.DubboManager;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class DubboStarter implements ServerFace {
	
	@Inject("refer:$ioc")
	protected Ioc ioc;

	@Override
	public void start() throws Exception {
		ioc.get(DubboManager.class);
	}
}
