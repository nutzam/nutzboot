package org.nutz.boot.starter.nutz.mvc;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.WebFilterFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.WhaleFilter;

@IocBean
public class WhaleFilterStarter implements WebFilterFace {

    @PropDoc(value="在其他Filter之前设置input编码", defaultValue="UTF-8")
    public static final String PROP_ENC_INPUT = "nutz.mvc.whale.enc.input";
    @PropDoc(value="在其他Filter之前设置output编码", defaultValue="UTF-8")
    public static final String PROP_ENC_OUTPUT = "nutz.mvc.whale.enc.output";
    @PropDoc(value="隐形http方法参数转换所对应的参数名")
    public static final String PROP_HIDDEN_METHOD_PARAM = "nutz.mvc.whale.http.hidden_method_param";
    @PropDoc(value="是否允许使用X-HTTP-Method-Override", defaultValue="false")
    public static final String PROP_HTTP_METHOD_OVERRIDE = "nutz.mvc.whale.http.method_override";
    @PropDoc(value="是否启用隐形Upload支持", defaultValue="false")
    public static final String PROP_HIDDEN_UPLOAD_ENABLE = "nutz.mvc.whale.upload.enable";
	
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
        params.put("enc.input", conf.get(PROP_ENC_INPUT, "UTF-8"));
        params.put("enc.output", conf.get(PROP_ENC_OUTPUT, "UTF-8"));
        if (conf.has(PROP_HIDDEN_METHOD_PARAM)) {
        	params.put("http.hidden_method_param", conf.get(PROP_HIDDEN_METHOD_PARAM));
        }
        if (conf.has(PROP_HTTP_METHOD_OVERRIDE)) {
        	params.put("http.method_override", conf.get(PROP_HTTP_METHOD_OVERRIDE));
        }
        if (conf.has(PROP_HIDDEN_UPLOAD_ENABLE)) {
        	params.put("upload.enable", conf.get(PROP_HIDDEN_UPLOAD_ENABLE));
        }
        return params;
    }

    public int getOrder() {
        return conf.getInt("web.filter.order.whale", FilterOrder.WhaleFilter);
    }
}
