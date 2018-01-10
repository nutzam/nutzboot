package io.nutz.demo.feign.module;

import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.DELETE;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import io.nutz.demo.feign.bean.User;

/**
 * 这个给nutzboot-demo-cloud-client调用的模块
 *
 */
@IocBean
@At("/user")
@Ok("json:full")
@AdaptBy(type=JsonAdaptor.class)
public class UserJsonModule {

    @Inject
    protected Dao dao;

    @At
    public List<User> list() {
        return dao.query(User.class, null);
    }

    @At
    public User fetch(@Param("id")int id) {
        return dao.fetch(User.class, id);
    }

    @POST
    public User add(@Param("name")String name, @Param("age")int age) {
        User user = new User();
        user.setName(name);
        user.setAge(age);
        return dao.insert(user);
    }

    @DELETE
    public void delete(@Param("id")int id) {
        dao.clear(User.class, Cnd.where("id", "=", id));
    }
    
}
