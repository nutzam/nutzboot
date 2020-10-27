package org.nutz.boot.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.nutz.boot.test.entity.UserDo;
import org.nutz.http.Http;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@NutzBootTest
public class TestUserModule {

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
    void test_service_resources_add(UserDo argument){
        Map<String,Object> params= Json.fromJson(Map.class, Json.toJson(argument));
        String content= Http.post("http://localhost:8088/user/add",params,10000);
        NutMap result = Json.fromJson(NutMap.class,content);
        assertTrue(result.getBoolean("ok"));
    }


}
