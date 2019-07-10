package io.nutz.cloud.demo.service.impl;

import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.Param;

import io.nutz.cloud.demo.bean.User;
import io.nutz.cloud.demo.service.UserService;

@IocBean
public class UserServiceImpl implements UserService {

    @Inject
    protected Dao dao;

    public List<User> list() {
        return dao.query(User.class, null);
    }

    public User fetch(@Param("id")int id) {
        return dao.fetch(User.class, id);
    }

    public User add(@Param("name")String name, @Param("age")int age) {
        User user = new User();
        user.setName(name);
        user.setAge(age);
        return dao.insert(user);
    }

    public void delete(@Param("id")int id) {
        dao.clear(User.class, Cnd.where("id", "=", id));
    }

    public void ping() {
    }

}
