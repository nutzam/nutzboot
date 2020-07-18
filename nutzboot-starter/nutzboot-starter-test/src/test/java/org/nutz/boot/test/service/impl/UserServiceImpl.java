package org.nutz.boot.test.service.impl;

import org.nutz.boot.test.entity.UserDo;
import org.nutz.boot.test.service.UserService;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(name="userService")
public class UserServiceImpl implements UserService {
    @Override
    public void save(UserDo user) {
        //TODO some thing
    }
}
