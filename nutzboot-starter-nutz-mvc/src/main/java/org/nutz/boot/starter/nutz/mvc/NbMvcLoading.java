package org.nutz.boot.starter.nutz.mvc;

import org.nutz.boot.AppContext;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Stopwatch;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.LoadingException;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.mvc.UrlMapping;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.impl.NutLoading;

public class NbMvcLoading extends NutLoading {

    private static final Log log = Logs.get();
    
    public UrlMapping load(NutConfig config) {
        config.setMainModule(AppContext.getDefault().getMainClass());
        return super.load(config);
    }

    protected Ioc createIoc(NutConfig config, Class<?> mainModule) throws Exception {
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
    
    public void depose(NutConfig config) {
        if (log.isInfoEnabled())
            log.infof("Nutz.Mvc[%s] is deposing ...", config.getAppName());
        Stopwatch sw = Stopwatch.begin();

        // Firstly, upload the user customized desctroy
        try {
            Setup setup = config.getAttributeAs(Setup.class, Setup.class.getName());
            if (null != setup)
                setup.destroy(config);
        }
        catch (Exception e) {
            throw new LoadingException(e);
        }

        // Done, print info
        sw.stop();
        if (log.isInfoEnabled())
            log.infof("Nutz.Mvc[%s] is down in %sms", config.getAppName(), sw.getDuration());
    }
}
