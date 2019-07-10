package io.nutz.cloud.demo.service;

import java.util.List;

import org.nutz.boot.starter.literpc.annotation.RpcService;

import io.nutz.cloud.demo.bean.User;

@RpcService
public interface UserService {

    List<User> list();

    User fetch(int id);

    User add(String name, int age);

    void delete(int id);
    
    void ping();
}
