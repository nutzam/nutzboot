package org.nutz.boot.starter.nutz.mvc;

import org.nutz.boot.AppContext;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.SessionProvider;
import org.nutz.mvc.UrlMapping;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.annotation.SessionBy;
import org.nutz.mvc.impl.NutLoading;

public class NbMvcLoading extends NutLoading {
    
    public UrlMapping load(NutConfig config) {
        config.setMainModule(AppContext.getDefault().getMainClass());
        return super.load(config);
    }

    protected Ioc createIoc(NutConfig config, Class<?> mainModule) throws Exception {
        if (mainModule.getAnnotation(IocBy.class) != null)
            return super.createIoc(config, mainModule);
        Ioc ioc = AppContext.getDefault().getIoc();
        Mvcs.setIoc(ioc);
        return ioc;
    }
    
    protected void evalLocalization(NutConfig config, Class<?> mainModule) {
        if (mainModule.getAnnotation(Localization.class) != null)
            super.evalLocalization(config, mainModule);
        else
            super.evalLocalization(config, NbMainModule.class);
    }
    
    protected void createSessionProvider(NutConfig config, Class<?> mainModule) throws Exception {
        SessionBy sb = mainModule.getAnnotation(SessionBy.class);
        if (sb != null)
            super.createSessionProvider(config, mainModule);
        else {
            Ioc ioc = AppContext.getDefault().getIoc();
            if (ioc.has("sessionProvider")) {
                config.setSessionProvider(ioc.get(SessionProvider.class, "sessionProvider"));
            }
        }
    }
}
