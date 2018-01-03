package org.nutz.boot.starter.shiro;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.apache.shiro.web.servlet.ShiroFilter;
import org.nutz.boot.AppContext;
import org.nutz.boot.starter.WebFilterFace;
import org.nutz.integration.shiro.ShiroFilter2;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class ShiroFilterStarter implements WebFilterFace {
	
	@Inject("refer:$ioc")
	protected Ioc ioc;
	
	@Inject
	protected PropertiesProxy conf;
	
	@Inject
	protected AppContext appContext;

	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
		ShiroUtil.appContext = appContext;
	}

    public String getName() {
        return "shiro";
    }

    public String getPathSpec() {
        return "/*";
    }

    public EnumSet<DispatcherType> getDispatches() {
        return EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR, DispatcherType.ASYNC);
    }
    
    @IocBean(name="shiroFilter")
    public ShiroFilter createShiroFilter() {
    	return new ShiroFilter2();
    }

    public Filter getFilter() {
        return ioc.get(ShiroFilter.class, "shiroFilter");
    }

    public Map<String, String> getInitParameters() {
        return new HashMap<>();
    }

    public int getOrder() {
        return conf.getInt("web.filter.order.shiro", FilterOrder.ShiroFilter);
    }
}
