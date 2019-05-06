package io.nutz.demo.simple.bean;

import io.swagger.annotations.ApiModel;

@ApiModel(description="普通用户")
public class User {

    private int id;
    private String name;
    private String password;
    private String salt;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getSalt() {
        return salt;
    }
    public void setSalt(String salt) {
        this.salt = salt;
    }
}
