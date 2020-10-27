package org.nutz.boot.test.module;

import org.nutz.boot.test.entity.UserDo;
import org.nutz.boot.test.service.UserService;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.filter.CrossOriginFilter;
@At("/user")
@IocBean
@Ok("json:full")
public class UserModule {
    @Inject
    UserService userService;

    @At
    @POST
    @Filters({@By(type= CrossOriginFilter.class)})
    public NutMap add(@Param("..") UserDo user) {
        if (Strings.isBlank(user.getId())) {
            return new NutMap("ok", false);
        }
        userService.save(user);
        return new NutMap("ok", true).setv("data", user);
    }


}
