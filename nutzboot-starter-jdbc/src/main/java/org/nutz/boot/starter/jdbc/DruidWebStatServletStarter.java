package org.nutz.boot.starter.jdbc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Servlet;

import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;

import com.alibaba.druid.support.http.StatViewServlet;

@IocBean
public class DruidWebStatServletStarter implements WebServletFace {
	
	@Inject
	protected PropertiesProxy conf;

	public String getName() {
		return "druid";
	}

	public String getPathSpec() {
		return "/druid/*";
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, String> getInitParameters() {
		Map<String, String> params = new HashMap<>();
		Map<String, Object> _tmp = Lang.filter((Map)conf.toMap(), "druid.webstat.servlet", null, null, null);
		for (Entry<String, Object> en : _tmp.entrySet()) {
			params.put(en.getKey(), String.valueOf(en.getValue()));
		};
		return params;
	}

	@Override
	public Servlet getServlet() {
		if (!DataSourceStarter.isDruid(conf))
			return null;
		return new StatViewServlet();
	}

}
