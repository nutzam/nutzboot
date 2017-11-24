package org.nutz.boot.starter.shiro;

import java.util.EventListener;

import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.nutz.boot.starter.WebEventListenerFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class ShiroEnv implements WebEventListenerFace {

	@Inject("refer:$ioc")
	protected Ioc ioc;
	
	@IocBean(name="shiroEnvironmentLoaderListener")
	public EnvironmentLoaderListener createShiroEnvironmentLoaderListener() {
		return new EnvironmentLoaderListener();
	}
	
	public EventListener getEventListener() {
		return ioc.get(EnvironmentLoaderListener.class, "shiroEnvironmentLoaderListener");
	}
}
