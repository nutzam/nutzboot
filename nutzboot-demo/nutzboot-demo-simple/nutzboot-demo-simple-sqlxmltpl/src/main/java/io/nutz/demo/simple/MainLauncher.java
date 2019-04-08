package io.nutz.demo.simple;

import com.github.threefish.nutz.dto.PageDataDTO;
import io.nutz.demo.simple.bean.User;
import io.nutz.demo.simple.service.IUserService;
import org.nutz.boot.NbApp;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.util.NutMap;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date 2019-04-03
 */
@IocBean(create = "init")
public class MainLauncher {

    @Inject
    protected Dao dao;
    @Inject
    protected IUserService userService;

    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

    public void init() {
        dao.create(User.class, true);
        dao.insert(new User("apple", 40, "北京"));
        dao.insert(new User("ball", 30, "未知"));
        dao.insert(new User("cat", 50, "温哥华"));
        dao.insert(new User("fox", 51, "纽约"));
        dao.insert(new User("bra", 25, "济南"));
        dao.insert(new User("alina", 50, "深圳"));
        PageDataDTO dataDTO = userService.queryLikeName(NutMap.NEW().setv("name", "a%"), new Pager(0, 5));
        List<User> userList = userService.queryLikeNameByCnd(Cnd.where("name", "like", "a%"), new Pager(0, 5));
        List<NutMap> userMaps = userService.queryMapslikeName(NutMap.NEW());
        System.out.printf("queryLikeName        --> %s %s \n", dataDTO.getCount(), Json.toJson(dataDTO.getData(), JsonFormat.compact()));
        System.out.printf("queryLikeNameByCnd   --> %s \n", Json.toJson(userList, JsonFormat.compact()));
        System.out.printf("queryMapslikeName    --> %s \n", Json.toJson(userMaps, JsonFormat.compact()));
    }

}
