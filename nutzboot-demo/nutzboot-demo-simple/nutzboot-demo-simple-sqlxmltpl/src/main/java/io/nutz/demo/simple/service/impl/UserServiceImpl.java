package io.nutz.demo.simple.service;

import com.github.threefish.nutz.sqltpl.ISqlDaoExecuteService;
import com.github.threefish.nutz.sqltpl.SqlsTplHolder;
import io.nutz.demo.simple.bean.User;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.service.EntityService;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/3
 */
@IocBean
public class UserServiceImpl<T> extends EntityService<T> implements IUserService<User>, ISqlDaoExecuteService {
    @Inject
    protected Dao dao;
    /**
     * 1、我是必须要有的
     * 2、可以不实现 ISqlDaoExecuteService 接口，用 SqlsTplHolder 直接渲染sql自己再进行操作
     */
    private SqlsTplHolder sqlsTplHolder;

    @Override
    public SqlsTplHolder getSqlsTplHolder() {
        return this.sqlsTplHolder;
    }

    @Override
    public Dao getDao() {
        return this.dao;
    }
}
