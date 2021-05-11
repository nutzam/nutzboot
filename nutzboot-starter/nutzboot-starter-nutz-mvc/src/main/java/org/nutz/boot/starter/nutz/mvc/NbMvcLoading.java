package org.nutz.boot.starter.nutz.mvc;

import org.nutz.boot.AppContext;
import org.nutz.boot.starter.nutz.mvc.api.ActionLoaderFace;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.*;
import org.nutz.mvc.annotation.ChainBy;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.impl.Loadings;
import org.nutz.mvc.impl.NutActionChainMaker;
import org.nutz.mvc.impl.NutLoading;

import java.util.Set;

public class NbMvcLoading extends NutLoading {

    private static final Log log = Logs.get();

    protected AppContext appContext = AppContext.getDefault();

    public UrlMapping load(NutConfig config) {
        config.setMainModule(appContext.getMainClass());
        return super.load(config);
    }

    protected Ioc createIoc(NutConfig config, Class<?> mainModule) throws Exception {
        Ioc ioc = appContext.getIoc();
        Mvcs.setIoc(ioc);
        return ioc;
    }

    protected void evalLocalization(NutConfig config, Class<?> mainModule) {
        if (mainModule.getAnnotation(Localization.class) != null)
            super.evalLocalization(config, mainModule);
        else
            super.evalLocalization(config, NbMainModule.class);
    }

    protected Set<Class<?>> getModuleClasses(Ioc ioc, Class<?> mainModule, EntryDeterminer determiner) {
        Set<Class<?>> modules = super.getModuleClasses(ioc, mainModule, determiner);
        for (ActionLoaderFace face : appContext.getBeans(ActionLoaderFace.class)) {
            face.getActions(ioc, mainModule, determiner, modules);
        }
        return modules;
    }

    protected ActionChainMaker createChainMaker(NutConfig config, Class<?> mainModule) {
        ActionChainMaker maker;
        String chain = config.getInitParameter("chain");
        if (Strings.isNotBlank(chain)) {
            maker = new NutActionChainMaker(chain);
        } else {
            ChainBy ann = mainModule.getAnnotation(ChainBy.class);
            maker = null == ann ? new NutActionChainMaker(new String[]{})
                    : Loadings.evalObj(config, ann.type(), ann.args());
        }
        if (log.isDebugEnabled())
            log.debugf("@ChainBy(%s)", maker.getClass().getName());
        return maker;
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
        } catch (Exception e) {
            throw new LoadingException(e);
        }

        // Done, print info
        sw.stop();
        if (log.isInfoEnabled())
            log.infof("Nutz.Mvc[%s] is down in %sms", config.getAppName(), sw.getDuration());
    }
}
