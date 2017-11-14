package org.nutz.boot.starter.shiro;

import java.util.EventListener;

import org.nutz.boot.starter.WebEventListenerFace;

public class ShiroEnv implements WebEventListenerFace {

	public EventListener getEventListener() {
		return new org.apache.shiro.web.env.EnvironmentLoaderListener();
	}
}
