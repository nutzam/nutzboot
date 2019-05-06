package org.nutz.boot.starter.urule;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;

import org.nutz.boot.tools.SpringWebContextProxy;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.bstek.urule.Utils;
import com.bstek.urule.model.function.FunctionDescriptor;
import com.bstek.urule.model.library.action.SpringBean;
import com.bstek.urule.model.library.action.annotation.ActionBean;
import com.bstek.urule.runtime.BuiltInActionLibraryBuilder;

@IocBean(create="init")
public class UruleSpringEnvStarter extends SpringWebContextProxy {
    
    private static final Log log = Logs.get();
    
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

    @SuppressWarnings("unchecked")
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
        
        // 注入ActionBean试试
        try {
            String[] names = appContext.getIoc().getNamesByAnnotation(ActionBean.class);
            BuiltInActionLibraryBuilder builder = applicationContext.getBean(BuiltInActionLibraryBuilder.class);
            Method method = BuiltInActionLibraryBuilder.class.getDeclaredMethod("buildMethod", Method[].class);
            method.setAccessible(true);
            for (String name : names) {
                if (Strings.isBlank(name))
                    continue;
                Object obj = appContext.getIoc().get(null, name);
                Class<?> klass = obj.getClass();
                ActionBean ab = klass.getAnnotation(ActionBean.class);
                if (ab == null) {
                    klass = klass.getSuperclass();
                    ab = klass.getAnnotation(ActionBean.class);
                }
                log.debug("add ActionBean : " + klass.getName());
                SpringBean bean = new SpringBean();
                bean.setId(name);
                bean.setName(ab.name());
                bean.setMethods((List<com.bstek.urule.model.library.action.Method>) method.invoke(builder, new Object[] {klass.getMethods()}));
                builder.getBuiltInActions().add(bean);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
