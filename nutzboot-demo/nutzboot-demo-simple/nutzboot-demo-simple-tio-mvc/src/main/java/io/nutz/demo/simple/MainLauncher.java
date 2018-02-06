package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import io.nutz.demo.simple.bean.User;

/**
 * @Author wendal
 */
@IocBean(create="init")
public class MainLauncher {
    
    @Inject
    protected Dao dao;
    
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
		new NbApp().setPrintProcDoc(true).run();
	}

}
