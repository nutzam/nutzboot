package org.nutz.boot.starter;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.nutz.dao.impl.SimpleDataSource;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import com.alibaba.druid.pool.DruidDataSourceFactory;

@IocBean
public class DataSourceStarter {
    
    protected static String PRE = "nutz.dataSource.";

    @Inject
    protected PropertiesProxy conf;
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @IocBean
    public DataSource getDataSource() throws Exception {
        switch (conf.get(PRE+"type", "simple")) {
        case "simple":
        case "org.nutz.dao.impl.SimpleDataSource":
            SimpleDataSource simpleDataSource = new SimpleDataSource();
            String jdbcUrl = conf.get(PRE+"jdbcUrl", conf.get(PRE+"url"));
            if (Strings.isBlank(jdbcUrl)) {
                throw new RuntimeException("need "+PRE+".url");
            }
            simpleDataSource.setJdbcUrl(jdbcUrl);
            simpleDataSource.setUsername(conf.check(PRE+"username"));
            simpleDataSource.setPassword(conf.check(PRE+"password"));
            return simpleDataSource;
        case "druid":
        case "com.alibaba.druid.pool.DruidDataSource":
            if (Strings.isBlank(conf.get(PRE+"url"))) {
                throw new RuntimeException("need nutz.dataSource.url");
            }
            Map map = Lang.filter(new HashMap(conf.toMap()), PRE, null, null, null);
            return DruidDataSourceFactory.createDataSource(map);
        case "jndi":
            return (DataSource) ((Context)new InitialContext().lookup("java:comp/env")).lookup(conf.check(PRE+"value"));
        // TODO 支持其他数据源
        default:
            break;
        }
        throw new RuntimeException("not support nutz.dataSource.type=" + conf.get("nutz.dataSource.type"));
    }
}
