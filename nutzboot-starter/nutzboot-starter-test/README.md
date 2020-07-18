# nutzboot-starter-test 基于 JUnit 5 实现单元测试

简介(可用性:测试,维护者:邓华锋([http://dhf.me](http://dhf.me)))
==================================

添加依赖

本插件依赖

```xml
<dependency> 
	<groupId>org.nutz</groupId>
    <artifactId>nutzboot-starter-test</artifactId>
    <version>2.4.1-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

JUnit5依赖

```xml
<dependency>
	<groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
	<groupId>org.junit.jupiter</groupId>
	<artifactId>junit-jupiter-engine</artifactId>
	<scope>test</scope>
</dependency>
<dependency>
	<groupId>org.junit.jupiter</groupId>
	<artifactId>junit-jupiter-params</artifactId>
	<scope>test</scope>
</dependency>
<dependency>
	<groupId>org.junit.jupiter</groupId>
	<artifactId>junit-jupiter-migrationsupport</artifactId>
	<scope>test</scope>
</dependency>
<dependency>
	<groupId>org.junit.vintage</groupId>
	<artifactId>junit-vintage-engine</artifactId>
	<scope>test</scope>
</dependency>
<dependency>
	<groupId>org.mockito</groupId>
	<artifactId>mockito-core</artifactId>
	<scope>test</scope>
</dependency>
<dependency>
	<groupId>org.hamcrest</groupId>
	<artifactId>hamcrest-core</artifactId>
	<scope>test</scope>
</dependency>
<dependency>
	<groupId>org.mockito</groupId>
	<artifactId>mockito-core</artifactId>
	<scope>test</scope>
</dependency>
```




使用示例
----------------------------------------------
使用@NutzBootTest注解进行单元测试。
```Java
package org.nutz.boot.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.nutz.boot.test.entity.UserDo;
import org.nutz.boot.test.service.UserService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.lang.Strings;
import java.util.stream.Stream;

@NutzBootTest
public class TestUserService {
    @Inject
    UserService userService;

    static Stream<UserDo> addUser() {
        UserDo[] users=new UserDo[5];
        for (int i = 0; i <users.length ; i++) {
            UserDo user=new UserDo();
            user.setId("Junit5-"+i);
            user.setName("测试-"+i);
            users[i]=user;
        }
        return Stream.of(users);
    }


    @ParameterizedTest
    @Tag("user")
    @DisplayName("增加用户")
    @MethodSource("addUser")
    public void add(UserDo user) {
        if (Strings.isBlank(user.getId())) {
            return;
        }
        userService.save(user);
    }
}
```



后期功能待更新。