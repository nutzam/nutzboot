package io.nutz.demo.feign.service.fallback;

import java.util.ArrayList;
import java.util.List;

import org.nutz.ioc.loader.annotation.IocBean;

import io.nutz.demo.feign.bean.User;
import io.nutz.demo.feign.service.UserService;

@IocBean(name="userService_fallback")
public class FallbackUserService implements UserService {

    public List<User> list() {
        return new ArrayList<>();
    }

    public User fetch(int id) {
        return null;
    }

    public User add(String name, int age) {
        return null;
    }

    public void delete(int id) {
    }

}
