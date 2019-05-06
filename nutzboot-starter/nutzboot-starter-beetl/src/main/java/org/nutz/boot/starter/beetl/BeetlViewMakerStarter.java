package org.nutz.boot.starter.beetl;

import java.io.IOException;

import org.beetl.core.GroupTemplate;
import org.beetl.ext.nutz.BeetlViewMaker;
import org.beetl.ext.web.WebRender;
import org.nutz.boot.AppContext;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(name = "$views_beetl", create = "init")
public class BeetlViewMakerStarter extends BeetlViewMaker {

    private static final Log log = Logs.get();

    @Inject
    protected AppContext appContext;

    public BeetlViewMakerStarter() throws IOException {
        super();
    }

    public void init() throws IOException {
        if (appContext == null)
            return;
        log.debug("beetl init ....");
        groupTemplate = appContext.getIoc().get(GroupTemplate.class);
        render = new WebRender(groupTemplate);
    }
}
