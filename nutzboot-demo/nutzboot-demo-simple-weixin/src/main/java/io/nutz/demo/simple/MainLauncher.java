package io.nutz.demo.simple;

import java.util.List;

import org.nutz.boot.NbApp;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import io.nutz.demo.simple.bean.User;

@IocBean(create="init")
public class MainLauncher {
    
    @Inject
    Dao dao;
    
    @Ok("raw")
    @At("/user/count")
    public long count() {
        return dao.count(User.class);
    }
    
    @Ok("json:full")
    @At("/user/query")
    public List<User> query(@Param("..")Pager pager) {
        return dao.query(User.class, Cnd.orderBy().asc("age"), pager);
    }
    
    public void init() {
        dao.create(User.class, true);
        dao.insert(new User("apple", 40, "北京"));
        dao.insert(new User("ball", 30, "未知"));
        dao.insert(new User("cat", 50, "温哥华"));
        dao.insert(new User("fox", 51, "纽约"));
        dao.insert(new User("bra", 25, "济南"));
        dao.insert(new User("lina", 50, "深圳"));
    }

    public static void main(String[] args) throws Exception {
        new NbApp(MainLauncher.class).run();
    }

}
