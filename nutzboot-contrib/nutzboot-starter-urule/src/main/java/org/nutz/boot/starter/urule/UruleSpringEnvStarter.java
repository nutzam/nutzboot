package org.nutz.boot.starter.urule;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;

import org.nutz.boot.tools.SpringWebContextProxy;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;

import com.bstek.urule.Utils;
import com.bstek.urule.model.function.FunctionDescriptor;

@IocBean(create="init")
public class UruleSpringEnvStarter extends SpringWebContextProxy {
    
    @Inject
    protected PropertiesProxy conf;

    public UruleSpringEnvStarter() {
        configLocation = "classpath:urule-spring-context.xml";
        selfName = "urule";
    }

    public void init() {
        if (conf.has("urule.repository.dir")) {
            String dir = conf.get("urule.repository.dir");
            dir = Files.createDirIfNoExists(dir).getAbsolutePath();
            conf.set("rule.repository.di", dir);
        }
    }

    protected List<String> getSpringBeanNames() {
        List<String> names = super.getSpringBeanNames();
        names.remove(selfName + ".props");
        return names;
    }

    public void contextInitialized(ServletContextEvent sce) {
        super.contextInitialized(sce);
        Map<String, FunctionDescriptor> functionDescriptorMap = Utils.getFunctionDescriptorMap();
        Map<String, FunctionDescriptor> functionDescriptorLabelMap = Utils.getFunctionDescriptorLabelMap();
        for (FunctionDescriptor fun : appContext.getBeans(FunctionDescriptor.class)) {
            if(fun.isDisabled()){
                continue;
            }
            functionDescriptorMap.put(fun.getName(), fun);
            functionDescriptorLabelMap.put(fun.getLabel(), fun);
        }
        // TODO 待urule调整Utils中DebugWriter的写法后,兼容DebugWriter的注入
    }
}
