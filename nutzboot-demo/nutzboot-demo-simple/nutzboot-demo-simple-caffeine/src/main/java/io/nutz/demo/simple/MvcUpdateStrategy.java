package io.nutz.demo.simple;

import org.nutz.boot.starter.caffeine.UpdateStrategy;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.mvc.Mvcs;

@IocBean
public class MvcUpdateStrategy implements UpdateStrategy {

	@Override
	public boolean shouldUpdate(String key) {
		// 凡是request中携带update=true，都强制更新缓存
		return Lang.parseBoolean(Mvcs.getReq().getParameter("update"));
	}

	@Override
	public boolean invalidateAll(String key) {
		// 凡是request中携带invalidateAll=true，都强制更新缓存
		return Lang.parseBoolean(Mvcs.getReq().getParameter("invalidateAll"));
	}

}
