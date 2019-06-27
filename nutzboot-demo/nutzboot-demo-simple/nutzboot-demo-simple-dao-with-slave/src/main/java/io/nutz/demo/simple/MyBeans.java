package io.nutz.demo.simple;

import javax.sql.DataSource;

import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import com.alibaba.druid.pool.DruidDataSource;

@IocBean
public class MyBeans {

    @Inject
    protected PropertiesProxy conf;
    
    @IocBean
    public Dao dao2(@Inject DataSource dataSource2) {
        return new NutDao(dataSource2);
    }
    

    @IocBean
    public DataSource dataSource2(@Inject DataSource dataSource2) {
        return conf.make(DruidDataSource.class, "jdbc2.");
    }
}
