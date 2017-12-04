package io.nutz.demo.simple.bean;

import org.nutz.mongo.annotation.MoField;

public class User {

	@MoField("nm")
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
