package org.nutz.boot.starter.jdbc;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.WebFilterFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;

import com.alibaba.druid.support.http.WebStatFilter;

@IocBean
public class DruidWebStatFilterStarter implements WebFilterFace {
	
	protected static final String PRE = "druid.web.filter.";

    @PropDoc(group="driud", value="需要排除的路径")
    public static final String PROP_EXCLUSIONS = PRE + WebStatFilter.PARAM_NAME_EXCLUSIONS;
    
    @PropDoc(group="driud", value="是否开启性能监控")
    public static final String PROP_PROFILE_ENABLE = PRE + WebStatFilter.PARAM_NAME_PROFILE_ENABLE;
    
    @PropDoc(group="driud", value="是否开启session状态监控", defaultValue="true")
    public static final String PROP_NAME_SESSION_STAT_ENABLE = PRE + WebStatFilter.PARAM_NAME_SESSION_STAT_ENABLE;
    
    @PropDoc(group="driud", value="session最大状态数量")
    public static final String PROP_NAME_SESSION_STAT_MAX_COUNT = PRE + WebStatFilter.PARAM_NAME_SESSION_STAT_MAX_COUNT;
    
    @PropDoc(group="driud", value="用户权限信息的session属性名称")
    public static final String PROP_NAME_PRINCIPAL_SESSION_NAME = PRE + WebStatFilter.PARAM_NAME_PRINCIPAL_SESSION_NAME;

    @PropDoc(group="driud", value="用户权限信息的cookie属性名称")
    public static final String PROP_NAME_PRINCIPAL_COOKIE_NAME = PRE + WebStatFilter.PARAM_NAME_PRINCIPAL_COOKIE_NAME;
    
    @PropDoc(group="driud", value="Header中ReadIp对应的key")
    public static final String PROP_NAME_REAL_IP_HEADER = PRE + WebStatFilter.PARAM_NAME_REAL_IP_HEADER;
	
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
		Map<String, Object> _tmp = Lang.filter((Map)conf.toMap(), PRE, null, null, null);
		for (Entry<String, Object> en : _tmp.entrySet()) {
			params.put(en.getKey(), String.valueOf(en.getValue()));
		}
		return params;
	}
    public int getOrder() {
        return conf.getInt("web.filter.order.druid", FilterOrder.DruidFilter);
    }
}
