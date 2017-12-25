package io.nutz.demo.feign.service;

import java.util.List;

import feign.Param;
import feign.RequestLine;
import io.nutz.demo.feign.bean.User;

public interface UserService {

    @RequestLine("GET /user/list")
    List<User> list();

    @RequestLine("GET /user/fetch")
    User fetch(@Param("id") int id);

    @RequestLine("POST /user/add")
    User add(@Param("name") String name, @Param("age") int age);

    @RequestLine("DELETE /user/delete")
    void delete(@Param("id") int id);
}
