package io.nutz.demo.simple.service.impl;

import com.github.threefish.nutz.dto.PageDataDTO;
import com.github.threefish.nutz.sqltpl.ISqlDaoExecuteService;
import com.github.threefish.nutz.sqltpl.SqlsTplHolder;
import com.github.threefish.nutz.sqltpl.SqlsXml;
import io.nutz.demo.simple.bean.User;
import io.nutz.demo.simple.service.IUserService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.service.EntityService;

import java.util.List;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/3
 */
@IocBean(args = {"refer:dao"})
@SqlsXml
public class UserServiceImpl extends EntityService<User> implements IUserService, ISqlDaoExecuteService {

    /**
     * 1、我是必须要有的
     * 2、可以不实现 ISqlDaoExecuteService 接口，用 SqlsTplHolder 直接渲染sql自己再进行操作
     */
    private SqlsTplHolder sqlsTplHolder;

    public UserServiceImpl(Dao dao) {
        super(dao);
    }

    @Override
    public PageDataDTO queryLikeName(NutMap param, Pager pager) {
        return queryEntityBySql("queryLikeName", param, pager);
    }

    @Override
    public List<User> queryLikeNameByCnd(Cnd cnd, Pager pager) {
        return queryEntityBySql("queryLikeNameByCnd", NutMap.NEW(), cnd);
    }
    @Override
    public List<NutMap> queryMapslikeName(NutMap param) {
        return queryMapBySql("queryMapslikeName", param);
    }

    @Override
    public SqlsTplHolder getSqlsTplHolder() {
        return this.sqlsTplHolder;
    }

    @Override
    public Dao getDao() {
        return super.dao();
    }
}
