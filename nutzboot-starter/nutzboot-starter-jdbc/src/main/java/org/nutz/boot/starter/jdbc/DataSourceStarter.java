package org.nutz.boot.starter.jdbc;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.sql.DataSource;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.dao.impl.SimpleDataSource;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.util.DruidPasswordCallback;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@IocBean(depose="depose")
public class DataSourceStarter {

    private static final Log log = Logs.get();

    protected static String PRE = "jdbc.";
    @PropDoc(group = "jdbc", value = "连接池类型", possible = {"druid", "simple", "hikari"}, defaultValue = "druid")
    public static final String PROP_TYPE = PRE + "type";
    @PropDoc(group = "jdbc", value = "JDBC URL", need = true)
    public static final String PROP_URL = PRE + "url";
    @PropDoc(group = "jdbc", value = "数据库用户名")
    public static final String PROP_USERNAME = PRE + "username";
    @PropDoc(group = "jdbc", value = "数据库密码")
    public static final String PROP_PASSWORD = PRE + "password";
    // 其他属性请查阅druid/hikari的文档

    @Inject
    protected PropertiesProxy conf;

    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    // 保存已创建的Slave数据源
    protected static List<DataSource> slaves = new ArrayList<>();

    @IocBean
    public DataSource getDataSource() throws Exception {
        return createDataSource(ioc, conf, PRE);
    }

    @IocBean(name = "druidDataSource", depose = "close")
    public DataSource createDruidDataSource() throws Exception {
        if (Strings.isBlank(conf.get(PROP_URL))) {
            throw new RuntimeException("need jdbc.url");
        }
        return createDruidDataSource(conf, PRE);
    }

    @IocBean(name = "hikariDataSource", depose = "close")
    public DataSource createHikariCPDataSource() throws Exception {
        if (Strings.isBlank(conf.get(PROP_URL))) {
            throw new RuntimeException("need jdbc.url");
        }
        return createHikariCPDataSource(conf, PRE);
    }

    protected static boolean isDruid(PropertiesProxy conf) {
        String type = conf.get(PROP_TYPE, "druid");
        return "druid".equals(type) || "com.alibaba.druid.pool.DruidDataSource".equals(type);
    }

    public static DataSource createDataSource(Ioc ioc, PropertiesProxy conf, String prefix) {
        switch (conf.get(prefix + "type", "druid")) {
        case "simple":
        case "org.nutz.dao.impl.SimpleDataSource":
            SimpleDataSource simpleDataSource = new SimpleDataSource();
            String jdbcUrl = conf.get(PRE + "jdbcUrl", conf.get(PRE + "url"));
            if (Strings.isBlank(jdbcUrl)) {
                throw new RuntimeException("need " + PRE + ".url");
            }
            simpleDataSource.setJdbcUrl(jdbcUrl);
            simpleDataSource.setUsername(conf.get(PROP_USERNAME));
            simpleDataSource.setPassword(conf.get(PROP_PASSWORD));
            return simpleDataSource;
        case "druid":
        case "com.alibaba.druid.pool.DruidDataSource":
            DataSource ds = ioc.get(DataSource.class, "druidDataSource");
            String[] tmp = ioc.getNamesByType(DruidPasswordCallback.class);
            for (String beanName : tmp) {
				if (!Strings.isBlank(beanName))
					((DruidDataSource)ds).setPasswordCallback(ioc.get(DruidPasswordCallback.class, beanName));
			}
            return ds;
        case "hikari":
        case "com.zaxxer.hikari.HikariDataSource":
            return ioc.get(DataSource.class, "hikariDataSource");
        default:
            break;
        }
        throw new RuntimeException("not supported jdbc.type=" + conf.get("jdbc.type"));
    }

    public static DataSource createManyDataSource(Ioc ioc, PropertiesProxy conf, String prefix) {
        try {
            return createSlaveDataSource(ioc, conf, prefix);
        } catch (Exception e) {
            throw new RuntimeException("datasource init error", e);
        }
    }

    public static DataSource createSlaveDataSource(Ioc ioc, PropertiesProxy conf, String prefix) throws Exception {
        switch (conf.get(prefix + "type", "druid")) {
        case "simple":
        case "org.nutz.dao.impl.SimpleDataSource":
            SimpleDataSource simpleDataSource = new SimpleDataSource();
            String jdbcUrl = conf.get(PRE + "jdbcUrl", conf.get(PRE + "url"));
            if (Strings.isBlank(jdbcUrl)) {
                throw new RuntimeException("need " + PRE + ".url");
            }
            simpleDataSource.setJdbcUrl(jdbcUrl);
            simpleDataSource.setUsername(conf.get(PROP_USERNAME));
            simpleDataSource.setPassword(conf.get(PROP_PASSWORD));
            return simpleDataSource;
        case "druid":
        case "com.alibaba.druid.pool.DruidDataSource":
            return createDruidDataSource(conf, prefix);
        case "hikari":
        case "com.zaxxer.hikari.HikariDataSource":
            return createHikariCPDataSource(conf, prefix);
        default:
            break;
        }
        throw new RuntimeException("not supported jdbc.type=" + conf.get("jdbc.type"));
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static DataSource createDruidDataSource(PropertiesProxy conf, String prefix) throws Exception {
        Map map = Lang.filter(new HashMap(conf.toMap()), prefix, null, null, null);
        DruidDataSource dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(map);
        if (!conf.has(prefix + "filters"))
            dataSource.setFilters("stat");
        
        return dataSource;
    }

    public static DataSource createHikariCPDataSource(PropertiesProxy conf, String prefix) throws Exception {
        Properties properties = new Properties();
        for (String key : conf.keys()) {
            if (!key.startsWith(prefix) || key.equals(prefix + "type"))
                continue;
            if (key.equals(prefix + "url")) {
                if (!conf.has(prefix + "jdbcUrl"))
                    properties.put("jdbcUrl", conf.get(key));
            } else {
                properties.put(key.substring(5), conf.get(key));
            }
        }
        return new HikariDataSource(new HikariConfig(properties));
    }

    public static DataSource getSlaveDataSource(Ioc ioc, PropertiesProxy conf, String prefix) {
        if (ioc.has("slaveDataSource")) {
            return ioc.get(DataSource.class, "slaveDataSource");
        } else {
            return _getSlaveDataSource(ioc, conf, prefix);
        }
    }

    public static DataSource getManySlaveDataSource(Ioc ioc, PropertiesProxy conf, String prefix) {
        if (ioc.has(prefix + "slaveDataSource")) {
            return ioc.get(DataSource.class, prefix + "slaveDataSource");
        } else {
            return _getSlaveDataSource(ioc, conf, prefix);
        }
    }

    private static DataSource _getSlaveDataSource(Ioc ioc, PropertiesProxy conf, String prefix) {
        // 看看有多少从数据库被定义了
        List<DataSource> slaveDataSources = new ArrayList<>();
        for (String key : conf.keys()) {
            if (key.startsWith(prefix) && key.endsWith(".url")) {
                String slaveName = key.substring(prefix.length(), key.length() - ".url".length());
                log.debug("found Slave DataSource name=" + slaveName);
                try {
                    DataSource slaveDataSource = DataSourceStarter.createSlaveDataSource(ioc, conf, prefix + slaveName + ".");
                    slaveDataSources.add(slaveDataSource);
                    slaves.add(slaveDataSource);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        // 如果的确定义了从数据库集合
        if (slaveDataSources.size() > 0) {
            if (slaveDataSources.size() == 1) {
                // 单个? 那就直接set吧
                return slaveDataSources.get(0);
            } else {
                // 多个从数据源,使用DynaDataSource进行随机挑选
                // TODO 更多更精细的挑选策略(轮训/随机/可用性...)
                return new DynaDataSource(new DynaDataSourceSeletor(slaveDataSources));
            }
        }
        return null;
    }
    
    protected static class DynaDataSourceSeletor implements Iterator<DataSource>, Closeable {
        protected Random random = new Random(System.currentTimeMillis());
        protected DataSource[] ds;

        public DynaDataSourceSeletor(List<DataSource> slaveDataSources) {
            ds = slaveDataSources.toArray(new DataSource[slaveDataSources.size()]);
        }
        
        public DataSource next() {
            return ds[random.nextInt(ds.length)];
        }

        public boolean hasNext() {
            return true;
        }

        public void close() throws IOException {
            for (DataSource dataSource : ds) {
                try {
                    if (dataSource instanceof Closeable)
                        ((Closeable) dataSource).close();
                }
                catch (Throwable e) {
                }
            }
        }
    }
    
    public void depose() {
        log.debug("shutdown slave datasource count=" + slaves.size());
        for (DataSource ds : slaves) {
            try {
                if (ds instanceof Closeable)
                    ((Closeable) ds).close();
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
