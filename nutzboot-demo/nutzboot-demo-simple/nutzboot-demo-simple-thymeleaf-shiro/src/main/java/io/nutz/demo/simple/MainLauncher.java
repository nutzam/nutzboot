package io.nutz.demo.simple;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.nutz.boot.NbApp;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

import io.nutz.demo.simple.bean.User;

@IocBean(create="init")
public class MainLauncher {
    
    @Inject
    protected Dao dao;

    @At({"/", "/home"})
    @Ok("th:/home.html")
    public NutMap index() {
        return NutMap.NEW().setv("name", "NB").setv("age", 18);
    }
    
    @Ok("raw")
    @At("/shiro/test")
    public boolean isAuthenticated(HttpSession session) {
        Subject subject = SecurityUtils.getSubject();
        return subject.isAuthenticated();
    }
    
    public void init() {
        Daos.createTablesInPackage(dao, User.class, false);
        dao.insert(newUser("admin", "123456"));
        dao.insert(newUser("wendal", "123123"));
    }
    
    protected static User newUser(String name, String password) {
        User user = new User();
        user.setName(name);
        user.setSalt(R.UU32());
        user.setPassword(new Sha256Hash(password, user.getSalt()).toHex());
        user.setCreateTime(new Date());
        return user;
    }

    public static void main(String[] args) throws Exception {
        new NbApp().run();
    }

}
