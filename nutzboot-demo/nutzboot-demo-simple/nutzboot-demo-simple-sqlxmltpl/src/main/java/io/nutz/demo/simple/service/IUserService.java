package io.nutz.demo.simple.service;

import com.github.threefish.nutz.dto.PageDataDTO;
import io.nutz.demo.simple.bean.User;
import org.nutz.dao.Cnd;
import org.nutz.dao.pager.Pager;
import org.nutz.lang.util.NutMap;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/3
 */
public interface IUserService {

    PageDataDTO queryLikeName(NutMap param, Pager pager);

    List<User> queryLikeNameByCnd(Cnd cnd, Pager pager);

    List<NutMap> queryMapslikeName(NutMap param);
}
