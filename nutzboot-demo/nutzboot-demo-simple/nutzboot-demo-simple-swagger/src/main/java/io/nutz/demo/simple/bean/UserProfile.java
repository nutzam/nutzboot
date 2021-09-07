package io.nutz.demo.simple.bean;

import io.swagger.annotations.ApiModel;

@ApiModel(description="用户详情")
public class UserProfile {

    private int userId;
    private int age;
    private String location;
    private String city;
    private String sex;
    
    public UserProfile() {
        // TODO Auto-generated constructor stub
    }
    
    public UserProfile(int userId, int age, String location, String city, String sex) {
        super();
        this.userId = userId;
        this.age = age;
        this.location = location;
        this.city = city;
        this.sex = sex;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
}
