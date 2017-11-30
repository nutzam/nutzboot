package org.nutz.boot.starter.jdbc;

import java.util.Map;

import javax.servlet.Servlet;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.alibaba.druid.support.http.StatViewServlet;

@IocBean
public class DruidWebStatServletStarter implements WebServletFace {
	
	private static final Log log = Logs.get();
	
	protected static final String PRE = "druid.web.servlet.";

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
	public Map<String, Object> getInitParameters() {
		Map<String, Object> params = Lang.filter((Map)conf.toMap(), "druid.webstat.servlet.", null, null, null);
		if (!params.containsKey(StatViewServlet.PARAM_NAME_USERNAME))
			params.put(StatViewServlet.PARAM_NAME_USERNAME, "druid");
		if (!params.containsKey(StatViewServlet.PARAM_NAME_PASSWORD)) {
			String pwd = R.UU32();
			params.put(StatViewServlet.PARAM_NAME_PASSWORD, pwd);
			log.infof("druid stat view random password=%s", pwd);
		}
		return params;
	}

	@Override
	public Servlet getServlet() {
		if (!DataSourceStarter.isDruid(conf))
			return null;
		return new StatViewServlet();
	}

}
