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

@NutzBootTest(webEnvironment = NutzBootTest.WebEnvironment.RANDOM_PORT)
public class TestUserService {
    @Inject
    UserService userService;

    static Stream<UserDo> addUser() {
        UserDo[] ServiceResouresDos=new UserDo[5];
        for (int i = 0; i <ServiceResouresDos.length ; i++) {
            UserDo serviceResoures=new UserDo();
            serviceResoures.setId("STest123-"+i);
            serviceResoures.setName("测试服务-"+i);
            ServiceResouresDos[i]=serviceResoures;
        }
        return Stream.of(ServiceResouresDos);
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
