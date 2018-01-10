package io.nutz.demo.simple.module;

import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Dao;
import org.nutz.integration.shiro.SimpleShiroToken;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import io.nutz.demo.simple.bean.User;

@At("/user")
@IocBean
public class UserModule {
    
    @Inject
    Dao dao;

    @GET
    @At("/login")
    @Ok(">>:/login.html")
    public void loginPage() {}
    
    
    @Ok("json")
    @Fail("http:500")
    @POST
    @At("/login")
    public boolean login(@Param("username")String username, @Param("password")String password, HttpSession session) {
        User user = dao.fetch(User.class, username);
        if (user == null)
            return false;
        Sha256Hash hash = new Sha256Hash(password, user.getSalt());
        if (!hash.toHex().equals(user.getPassword())) {
            return false;
        }
        Subject subject = SecurityUtils.getSubject();
        subject.login(new SimpleShiroToken(user.getId()));
        return true;
    }

    @Ok(">>:/index.html")
    @At
    public void logout() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated())
            subject.logout();
    }
}
