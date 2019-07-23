package org.nutz.boot.starter.mybatis;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.nutz.boot.AppContext;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class MybatisStarter {
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    @Inject
    protected AppContext appContext;
    
    @Inject
    protected PropertiesProxy conf;
    
    @IocBean
    public SqlSessionFactory createSqlSessionFactory() throws IOException {
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(Resources.getResourceAsStream(conf.get("mybatis.xmlpath", "mybatis-config.xml")));
        xmlConfigBuilder.parse();
        Configuration config = xmlConfigBuilder.getConfiguration();
        if (config.getEnvironment() == null) {
            Environment env = new Environment("default", new JdbcTransactionFactory(), ioc.get(DataSource.class));
            config.setEnvironment(env);
        }
        return new DefaultSqlSessionFactory(config);
    }
}
