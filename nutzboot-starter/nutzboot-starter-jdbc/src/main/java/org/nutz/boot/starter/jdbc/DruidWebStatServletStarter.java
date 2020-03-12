package org.nutz.boot.starter.jdbc;

import com.alibaba.druid.support.http.StatViewServlet;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import javax.servlet.Servlet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@IocBean
public class DruidWebStatServletStarter implements WebServletFace {
	
	private static final Log log = Logs.get();
	
	protected static final String PRE = "druid.web.servlet.";

	@PropDoc(group="druid", type="boolean", defaultValue="true", value="是否启动monitor页面")
	public static final String PROP_ENABLE = PRE + "enable";
	
	@PropDoc(group="druid", type="boolean", defaultValue="true", value="是否允许重置统计结果")
	public static final String PROP_RESET_ENABLE = PRE + StatViewServlet.PARAM_NAME_RESET_ENABLE;

	@PropDoc(group="druid", value="读取JMX信息的URL")
	public static final String PROP_RESET_JMX_URL = PRE + StatViewServlet.PARAM_NAME_JMX_URL;

	@PropDoc(group="druid", value="JMX的用户名")
	public static final String PROP_RESET_JMX_USERNAME = PRE + StatViewServlet.PARAM_NAME_JMX_USERNAME;

	@PropDoc(group="druid", value="JMX的密码")
	public static final String PROP_RESET_JMX_PASSWORD = PRE + StatViewServlet.PARAM_NAME_JMX_PASSWORD;

	@PropDoc(group="druid",value="允许访问的ip列表")
	public static final String PROP_ALLOW = PRE + StatViewServlet.PARAM_NAME_ALLOW;

	@PropDoc(group="druid", value="禁止访问的ip列表")
	public static final String PROP_DENY = PRE + StatViewServlet.PARAM_NAME_DENY;

	@PropDoc(group="druid", value="访问monitor页面的用户名", defaultValue="driud")
	public static final String PROP_USERNAME = PRE + StatViewServlet.PARAM_NAME_USERNAME;
	
	@PropDoc(group="druid", value="访问monitor页面的密码", defaultValue="随机值,打印在日志中")
	public static final String PROP_PASSWORD = PRE + StatViewServlet.PARAM_NAME_PASSWORD;
	
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
		Map<String, Object> _tmp = Lang.filter((Map)conf.toMap(), PRE, null, null, null);
		if (!_tmp.containsKey(StatViewServlet.PARAM_NAME_USERNAME))
			_tmp.put(StatViewServlet.PARAM_NAME_USERNAME, "druid");
		if (!_tmp.containsKey(StatViewServlet.PARAM_NAME_PASSWORD)) {
			String pwd = R.UU32();
			_tmp.put(StatViewServlet.PARAM_NAME_PASSWORD, pwd);
			log.infof("druid stat view random user=%s password=%s", _tmp.get(StatViewServlet.PARAM_NAME_USERNAME), pwd);
		}
		for (Entry<String, Object> en : _tmp.entrySet()) {
			params.put(en.getKey(), String.valueOf(en.getValue()));
		}
		return params;
	}

	@Override
	public Servlet getServlet() {
		if (!DataSourceStarter.isDruid(conf))
			return null;
		if (!conf.getBoolean(PROP_ENABLE, true)) {
			log.debug("druid monitor enable=false, disable it");
			return null;
		}
		return new StatViewServlet();
	}

}
