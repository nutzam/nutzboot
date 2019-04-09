package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import io.nutz.demo.simple.bean.User;

@IocBean(create = "init")
public class MainLauncher {

    @Inject
    protected Dao dao;

    public void init() {
        dao.create(User.class, false);
        if (dao.count(User.class) == 0) {
            User user = new User();
            user.setName("wendal");
            user.setAge(18);
            user.setLocation("广州");
            dao.insert(user);
        }
    }

    // 默认配置的数据库是h2database,而且用了内存模式, 每次启动都是新的空白数据库
    // 配置其他数据库时,请务必加上对应的驱动程序, h2的依赖可删除
    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

}
