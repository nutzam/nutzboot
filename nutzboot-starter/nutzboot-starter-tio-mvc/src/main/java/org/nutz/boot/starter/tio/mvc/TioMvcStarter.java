package org.nutz.boot.starter.tio.mvc;

import org.nutz.boot.AppContext;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.tio.http.server.HttpServerStarter;

/**
 *
 * @Author wendal
 */
@IocBean
public class TioMvcStarter implements ServerFace {

    @Inject
    private AppContext appContext;

    protected HttpServerStarter httpServerStarter;

    public void start() throws Exception {
        httpServerStarter = appContext.getIoc().get(HttpServerStarter.class);
        httpServerStarter.start();
    }

    public void stop() throws Exception {
        if (httpServerStarter != null)
            httpServerStarter.stop();
    }
}
