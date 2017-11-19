package org.nutz.boot.starter.jdbc;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.nutz.boot.starter.WebFilterFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;

import com.alibaba.druid.support.http.WebStatFilter;

@IocBean
public class DruidWebStatFilterStarter implements WebFilterFace {
	
	@Inject
	protected PropertiesProxy conf;

	public String getName() {
		return "druid";
	}

	public String getPathSpec() {
		return "/*";
	}

	public EnumSet<DispatcherType> getDispatches() {
		return EnumSet.of(DispatcherType.REQUEST);
	}

	public Filter getFilter() {
		if (!DataSourceStarter.isDruid(conf))
			return null;
		return new WebStatFilter(); // 估计没人会覆盖它了把
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, String> getInitParameters() {
		Map<String, String> params = new HashMap<>();
		Map<String, Object> _tmp = Lang.filter((Map)conf.toMap(), "druid.webstat.filter", null, null, null);
		for (Entry<String, Object> en : _tmp.entrySet()) {
			params.put(en.getKey(), String.valueOf(en.getValue()));
		};
		return params;
	}

}
