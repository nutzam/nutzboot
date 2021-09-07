package io.nutz.demo.simple;

import javax.servlet.http.HttpSession;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean
public class MainLauncher {
    
    private static final Log log = Logs.get();

    @Ok("raw")
    @At("/time/now")
    public long now() {
        return System.currentTimeMillis();
    }
    
    @Ok("json:full")
    @At("/session/id")
    public NutMap sessionId(HttpSession session) {
        if (session.isNew()) {
            session.setAttribute("nutzboot", "sayhi");
        }
        return new NutMap("id", session.getId()).setv("time", System.currentTimeMillis());
    }
    
    @Ok("void")
    @At("/session/invalidate")
    public void sessionInvalidate(HttpSession session) {
        session.invalidate();
    }

	public static void main(String[] args) throws Exception {
	    log.warn("本demo需要mysql数据库!!!! 详情看application.properties");
		new NbApp().setPrintProcDoc(true).run();
	}

}
