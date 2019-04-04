package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import io.nutz.demo.simple.bean.User;

@IocBean(create="init")
public class MainLauncher {
    
    @Inject
    protected Dao dao;

    @Inject
    protected Dao newsDao;

    @Inject
    protected Dao playDao;
    
    public void init() {
        // 默认数据库建表并添加数据
        dao.create(User.class, true);
        dao.insert(new User("apple", 40, "北京"));
        dao.insert(new User("ball", 30, "未知"));
        // news数据库建表并添加数据
        newsDao.create(User.class, true);
        newsDao.insert(new User("cat", 50, "温哥华"));
        newsDao.insert(new User("fox", 51, "纽约"));
        // play数据库建表并添加数据
        playDao.create(User.class, true);
        playDao.insert(new User("bra", 25, "济南"));
        playDao.insert(new User("lina", 50, "深圳"));
    }

    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

}
