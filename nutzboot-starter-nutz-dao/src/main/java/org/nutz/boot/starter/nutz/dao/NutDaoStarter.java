package org.nutz.boot.starter.nutz.dao;

import javax.sql.DataSource;

import org.nutz.dao.SqlManager;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.impl.NutDao;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class NutDaoStarter {
    
    @Inject
    protected PropertiesProxy conf;
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    @IocBean
    public SqlManager getSqlManager() {
        return new FileSqlManager(conf.get("nutz.dao.sqls.path", "sqls/"));
    }
    
    @IocBean(name="dao")
    public NutDao getDao(@Inject DataSource dataSource, @Inject SqlManager sqlManager) {
        NutDao dao = new NutDao(dataSource, sqlManager);
        if (conf.getBoolean("nutz.dao.cache.enable", false)) {
            // TODO 支持daocache
        }
        return dao;
    }

}
