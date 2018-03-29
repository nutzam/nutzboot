package io.nutz.cloud.demo.service;

import java.util.List;

import org.nutz.boot.starter.literpc.api.RpcService;

import io.nutz.cloud.demo.bean.User;

public interface UserService extends RpcService {

    List<User> list();

    User fetch(int id);

    User add(String name, int age);

    void delete(int id);
}
