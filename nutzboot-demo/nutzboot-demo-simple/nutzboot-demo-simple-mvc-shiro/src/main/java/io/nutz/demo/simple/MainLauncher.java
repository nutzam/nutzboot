package io.nutz.demo.simple;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.nutz.boot.NbApp;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.integration.shiro.SimpleShiroToken;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.random.R;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import io.nutz.demo.simple.bean.User;

@IocBean(create="init")
public class MainLauncher {
	
	@Inject
	protected Dao dao;
    
    @Ok("raw")
    @At("/time/now")
    public long now() {
        return System.currentTimeMillis();
    }
    
    @Ok("raw")
    @At("/shiro/test")
    public boolean isAuthenticated(HttpSession session) {
    	Subject subject = SecurityUtils.getSubject();
    	return subject.isAuthenticated();
    }
    
    @Ok("json")
    @Fail("http:500")
    @POST
    @At("/user/login")
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
    	new NbApp().setPrintProcDoc(true).run();
    }

}
