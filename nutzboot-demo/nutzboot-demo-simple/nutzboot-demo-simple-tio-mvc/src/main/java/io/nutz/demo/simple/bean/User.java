package io.nutz.demo.simple.bean;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("t_user")
public class User {

    @Id
    private long id;
    @Name
    private String name;
    private int age;
    private String location;
    
    public User() {
    }
    
    public User(String name, int age, String location) {
        super();
        this.name = name;
        this.age = age;
        this.location = location;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
}
