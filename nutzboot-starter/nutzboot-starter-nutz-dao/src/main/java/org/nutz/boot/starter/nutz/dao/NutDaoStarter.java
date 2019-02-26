package org.nutz.boot.starter.nutz.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.jdbc.DataSourceStarter;
import org.nutz.dao.SqlManager;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.sql.run.NutDaoRunner;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Regex;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.cache.dao.DaoCacheInterceptor;
import org.nutz.plugins.cache.dao.api.CacheSerializer;
import org.nutz.plugins.cache.dao.api.DaoCacheProvider;
import org.nutz.plugins.cache.dao.impl.convert.JavaCacheSerializer;
import org.nutz.plugins.cache.dao.impl.provider.AbstractDaoCacheProvider;
import org.nutz.plugins.cache.dao.impl.provider.EhcacheDaoCacheProvider;
import org.nutz.plugins.cache.dao.impl.provider.MemoryDaoCacheProvider;
import org.nutz.plugins.cache.dao.impl.provider.RedisDaoCacheProvider;

import net.sf.ehcache.CacheManager;
import redis.clients.jedis.JedisPool;

@IocBean(create="init")
public class NutDaoStarter {

    private static final Log log = Logs.get();

    protected static final String PRE = "nutz.dao.";

    @PropDoc(value = "是否打印dao的SQL日志", defaultValue = "true", type = "boolean")
    public static final String PROP_INTERCEPTOR_LOG_ENABLE = PRE + "interceptor.log.enable";

    @PropDoc(value = "是否打印dao的SQL耗时日志", defaultValue = "false", type = "boolean")
    public static final String PROP_INTERCEPTOR_TIME_ENABLE = PRE + "interceptor.time.enable";

    @PropDoc(value = "sql目录", defaultValue = "sqls/")
    public static final String PROP_SQLS_PATH = PRE + "sqls.path";

    @PropDoc(value = "是否使用daocache", defaultValue = "false", type = "boolean")
    public static final String PROP_INTERCEPTOR_CACHE_ENABLE = PRE + "interceptor.cache.enable";

    @PropDoc(value = "daocache提供者", defaultValue = "memory", possible = {"memory", "ehcache", "jedis", "ioc"})
    public static final String PROP_INTERCEPTOR_CACHE_PROVIDER_TYPE = PRE + "interceptor.cache.provider.type";

    @PropDoc(value = "daocache提供者MemoryDaoCacheProvider的默认缓存大小")
    public static final String PROP_INTERCEPTOR_CACHE_PROVIDER_MEMORY_CACHE_SIZE = PRE + "interceptor.cache.provider.memory.cacheSize";

    @PropDoc(value = "daocache提供者DaoCacheProvider的IocBean名称", defaultValue = "daoCacheProvider")
    public static final String PROP_INTERCEPTOR_CACHE_PROVIDER_IOC_NAME = PRE + "interceptor.cache.provider.ioc.name";

    @PropDoc(value = "需要缓存的表名称,英文逗号分隔")
    public static final String PROP_INTERCEPTOR_CACHE_TABLE_NAMES = PRE + "interceptor.cache.table.names";
    @PropDoc(value = "需要缓存的表名称的正则表达式")
    public static final String PROP_INTERCEPTOR_CACHE_TABLE_PATTERN = PRE + "interceptor.cache.table.pattern";

    @PropDoc(value = "打印daocache详细调试日志", defaultValue = "false", type = "boolean")
    public static final String PROP_INTERCEPTOR_CACHE_DEBUG = PRE + "interceptor.cache.debug";

    @PropDoc(value = "事务内是否启用daocache", defaultValue = "false", type = "boolean")
    public static final String PROP_INTERCEPTOR_CACHE_ENABLE_WHEN_TRANS = PRE + "interceptor.cache.enableWhenTrans";

    @PropDoc(value = "是否缓存null结果", defaultValue = "true", type = "boolean")
    public static final String PROP_INTERCEPTOR_CACHE_CACHE4NULL = PRE + "interceptor.cache.cache4Null";

    @Inject
    protected PropertiesProxy conf;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    public void init() {
        injectManyDao();
    }

    @IocBean
    public SqlManager getSqlManager() {
        return new FileSqlManager(conf.get("nutz.dao.sqls.path", "sqls/"));
    }

    @IocBean(name = "daoCacheSerializer")
    public CacheSerializer createCacheSerializer() {
        return new JavaCacheSerializer();
    }

    @IocBean(name = "daoCacheInterceptor")
    public DaoCacheInterceptor createDaoCacheInterceptor() {
        DaoCacheInterceptor daoCacheInterceptor = new DaoCacheInterceptor();
        DaoCacheProvider provider = null;
        // 默认走内容就好了吧
        String daoCacheProviderType = conf.get(PROP_INTERCEPTOR_CACHE_PROVIDER_TYPE, "memory");
        switch (daoCacheProviderType) {
        // ehcache,最先支持的版本
        case "ehcache": {
            EhcacheDaoCacheProvider _provider = new EhcacheDaoCacheProvider();
            _provider.setCacheManager((CacheManager) getCacheManager());
            provider = _provider;
            break;
        }
        // 走redis
        case "jedis": {
            RedisDaoCacheProvider _provider = new RedisDaoCacheProvider();
            _provider.setJedisPool(ioc.get(JedisPool.class));
            provider = _provider;
            break;
        }
        // ioc自行提供?
        case "ioc": {
            provider = ioc.get(DaoCacheProvider.class, conf.get(PROP_INTERCEPTOR_CACHE_PROVIDER_IOC_NAME, "daoCacheProvider"));
            break;
        }
        // 不认识??
        default:
            log.warnf("not supprt [%s], fallback to memory", daoCacheProviderType);
        case "memory": { // 走内存
            MemoryDaoCacheProvider _provider = new MemoryDaoCacheProvider();
            if (conf.has(PROP_INTERCEPTOR_CACHE_PROVIDER_MEMORY_CACHE_SIZE))
                _provider.setCacheSize(conf.getInt(PROP_INTERCEPTOR_CACHE_PROVIDER_MEMORY_CACHE_SIZE));
            provider = _provider;
            break;
        }
        }
        // 如果继承了AbstractDaoCacheProvider,那肯定需要序列化器.
        if (provider instanceof AbstractDaoCacheProvider) {
            ((AbstractDaoCacheProvider) provider).setSerializer(ioc.get(CacheSerializer.class, "daoCacheSerializer"));
        }
        // 需要缓存哪些表呢?
        // 首先是直接写表名
        for (String tableName : Strings.splitIgnoreBlank(conf.get(PROP_INTERCEPTOR_CACHE_TABLE_NAMES, ""))) {
            daoCacheInterceptor.addCachedTableName(Strings.trim(tableName));
        }
        // 然后是正则表达式
        if (conf.has(PROP_INTERCEPTOR_CACHE_TABLE_PATTERN)) {
            daoCacheInterceptor.setCachedTableNamePatten(conf.get(PROP_INTERCEPTOR_CACHE_TABLE_PATTERN));
        }
        // 是否在事务中使用缓存
        if (conf.getBoolean(PROP_INTERCEPTOR_CACHE_ENABLE_WHEN_TRANS)) {
            daoCacheInterceptor.setEnableWhenTrans(true);
        }
        // 是否启用详细的调试日志呢?
        if (conf.getBoolean(PROP_INTERCEPTOR_CACHE_DEBUG, false)) {
            DaoCacheInterceptor.DEBUG = true;
        }
        // 是否缓存null
        daoCacheInterceptor.setCache4Null(conf.getBoolean(PROP_INTERCEPTOR_CACHE_CACHE4NULL, true));
        daoCacheInterceptor.setCacheProvider(provider);
        return daoCacheInterceptor;
    }

    @IocBean(name = "dao")
    public NutDao getDao(@Inject DataSource dataSource, @Inject SqlManager sqlManager) {
        NutDao dao = new NutDao(dataSource, sqlManager);
        List<Object> interceptors = new ArrayList<>();
        // 是否启用了DaoCache呢?
        if (conf.getBoolean(PROP_INTERCEPTOR_CACHE_ENABLE, false)) {
            interceptors.add(ioc.get(DaoCacheInterceptor.class));
        }
        // 日志拦截器
        if (conf.getBoolean(PROP_INTERCEPTOR_LOG_ENABLE, true)) {
            interceptors.add("log");
        }
        // sql耗时拦截器
        if (conf.getBoolean(PROP_INTERCEPTOR_TIME_ENABLE, true)) {
            interceptors.add("time");
        }
        // TODO 自定义其他拦截器?

        // 将拦截器赋予dao对象
        dao.setInterceptors(interceptors);
        // 看看是不是需要注入从数据库
        if (Lang.loadClassQuite("org.nutz.boot.starter.jdbc.DataSourceStarter") != null) {
            DataSource slaveDataSource = DataSourceStarter.getSlaveDataSource(ioc, conf, "jdbc.slave.");
            if (slaveDataSource != null) {
                NutDaoRunner runner = new NutDaoRunner();
                runner.setSlaveDataSource(slaveDataSource);
                dao.setRunner(runner);
            }
        }
        return dao;
    }

    private void injectManyDao() {
        // 正则匹配多数据库url
        String regex = "jdbc\\.many\\.(\\w*)\\.url";
        for (String key : conf.getKeys()) {
            Pattern pattern = Regex.getPattern(regex);
            Matcher match = pattern.matcher(key);
            if(match.find()) {
                // 获取数据库名称
                try {
                    String name = match.group(1);
                    String prefix_name = "jdbc.many." + name + ".";
                    DataSource manyDataSource = DataSourceStarter.createSlaveDataSource(ioc, conf, prefix_name);
                    NutDao nutDao = new NutDao();
                    nutDao.setDataSource(manyDataSource);
                    // 处理对应的从库
                    String slave_prefix = prefix_name + "slave.";
                    DataSource slaveDataSource = DataSourceStarter.getManySlaveDataSource(ioc, conf, slave_prefix);
                    if(slaveDataSource != null) {
                        NutDaoRunner runner = new NutDaoRunner();
                        runner.setSlaveDataSource(slaveDataSource);
                        nutDao.setRunner(runner);
                    }
                    // 加入到ioc对象
                    ioc.addBean(name + "Dao", nutDao);
                }
                catch (Exception e) {
                    throw new RuntimeException("datasource init error "+prefix_name, e);
                }
            }
        }
    }

    /**
     * 返回值不能是CacheManager,因为要考虑没有加ehcache的情况
     */
    protected Object getCacheManager() {
        CacheManager cacheManager = CacheManager.getInstance();
        if (cacheManager != null)
            return cacheManager;
        return CacheManager.newInstance();
    }
}
