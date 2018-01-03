package org.nutz.boot.starter.nutz.mvc;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.nutz.boot.starter.WebFilterFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.WhaleFilter;

@IocBean
public class WhaleFilterStarter implements WebFilterFace {
	
	
	@Inject("refer:$ioc")
	protected Ioc ioc;
	
	@Inject
	protected PropertiesProxy conf;

    public String getName() {
        return "whale";
    }

    public String getPathSpec() {
        return "/*";
    }

    public EnumSet<DispatcherType> getDispatches() {
        return EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE);
    }

    @IocBean(name="whaleFilter")
    public WhaleFilter createNutFilter() {
    	return new WhaleFilter();
    }
    
    public Filter getFilter() {
        return ioc.get(WhaleFilter.class, "whaleFilter");
    }

    public Map<String, String> getInitParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("enc.input", conf.get("nutz.mvc.whale.enc.input", "UTF-8"));
        params.put("enc.output", conf.get("nutz.mvc.whale.enc.output", "UTF-8"));
        if (conf.has("nutz.mvc.whale.http.hidden_method_param")) {
        	params.put("http.hidden_method_param", conf.get("nutz.mvc.whale.http.hidden_method_param"));
        }
        if (conf.has("nutz.mvc.whale.http.method_override")) {
        	params.put("http.method_override", conf.get("nutz.mvc.whale.http.method_override"));
        }
        if (conf.has("nutz.mvc.whale.upload.enable")) {
        	params.put("upload.enable", conf.get("nutz.mvc.whale.upload.enable"));
        }
        return params;
    }

    public int getOrder() {
        return conf.getInt("web.filter.order.whale", FilterOrder.WhaleFilter);
    }
}
