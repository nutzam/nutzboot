package io.nutz.demo.simple.module;

import javax.servlet.http.HttpSession;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean
public class ShiroModule {
    
    @Inject
    protected SecurityManager securityManager;

    @At("/sessionid/get")
    @Ok("raw")
    public String getSessionId(HttpSession session) {
        session.setAttribute("name", "wendal");
        return session.getId();
    }
    
    
    @At("/sessionid/?")
    @Ok("raw")
    public Object bySessionId(String sessionId) {
        Subject.Builder builder = new Subject.Builder(securityManager);
        builder.sessionId(sessionId);
        Subject subject = builder.buildSubject();
        Session session = subject.getSession();
        System.out.println(session.getAttribute("name"));
        return session;
    }
}
