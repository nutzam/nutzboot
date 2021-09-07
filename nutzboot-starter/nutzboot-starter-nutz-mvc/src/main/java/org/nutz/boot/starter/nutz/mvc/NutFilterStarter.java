package org.nutz.boot.starter.nutz.mvc;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.WebFilterFace;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.conf.NutConf;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.NutFilter;

@IocBean
public class NutFilterStarter implements WebFilterFace {

	@Inject("refer:$ioc")
	protected Ioc ioc;

    @PropDoc(value="过滤指定请求路径的正则表达式", defaultValue="")
    public static final String PROP_IGNORE = "nutz.mvc.ignore";
    @PropDoc(value="排除指定请求路径的正则表达式", defaultValue="")
    public static final String PROP_EXCLUSIONS = "nutz.mvc.exclusions";
    @PropDoc(value="指定NutFilter执行顺序", defaultValue="")
    public static final String PROP_WEB_FILTER_ORDER_NUTZ = "web.filter.order.nutz";
    @PropDoc(value="指定Chain文件路径", defaultValue="")
    public static final String PROP_WEB_FILTER_CHAIN = "web.filter.chain.path";

	@Inject
	protected PropertiesProxy conf;
	
	@Inject
	protected AppContext appContext;

    public String getName() {
        return "nutz";
    }

    public String getPathSpec() {
        return "/*";
    }

    public EnumSet<DispatcherType> getDispatches() {
        return EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE);
    }

    @IocBean(name="nutFilter")
    public NutFilter createNutFilter() {
        NutConf.set("nutz.mvc.whale.defaultpath", conf.get("nutz.mvc.whale.defaultpath", "./tmp/whale_upload/"));
    	return new NutFilter();
    }
    
    public Filter getFilter() {
        return ioc.get(NutFilter.class, "nutFilter");
    }

    public Map<String, String> getInitParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("modules", NbMainModule.class.getName());
        if (conf.has(PROP_IGNORE)) {
        	params.put("ignore", conf.get(PROP_IGNORE));
        }
        StringBuilder sb = new StringBuilder();
        for (WebServletFace face : appContext.getBeans(WebServletFace.class)) {
            for (String pathSpec : face.getPathSpecs()) {
                sb.append(',').append(pathSpec);
            }
        }
        params.put("exclusions", conf.get(PROP_EXCLUSIONS, "") + sb);
        params.put("chain", conf.get(PROP_WEB_FILTER_CHAIN, ""));
        return params;
    }

    public int getOrder() {
        return conf.getInt(PROP_WEB_FILTER_ORDER_NUTZ, FilterOrder.NutFilter);
    }
}
