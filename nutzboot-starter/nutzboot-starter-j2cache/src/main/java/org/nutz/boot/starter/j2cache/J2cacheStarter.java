package org.nutz.boot.starter.j2cache;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.J2CacheBuilder;
import net.oschina.j2cache.J2CacheConfig;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.IOException;
import java.util.Properties;

/**
 * @Author 蛋蛋(https://github.com/TopCoderMyDream)
 * @Time 2018年1月17日 20:20:35
 */
@IocBean
public class J2cacheStarter {
    private static final Log log = Logs.get();
    @Inject
    PropertiesProxy conf;

    protected static final String PRE = "j2cache.";

    @PropDoc(group = "j2cache", value = "广播类型,例如redis", defaultValue = "")
    public static final String PROP_BROADCAST = PRE + "broadcast";
    @PropDoc(group = "j2cache", value = "组播名", defaultValue = "")
    public static final String PROP_JGROUPS_CHANNEL_NAME = PRE + "jgroups.channel.name";
    @PropDoc(group = "j2cache", value = "Jgroups的配置文件", defaultValue = "")
    public static final String PROP_JGROUPS_CONFIXML = PRE + "jgroups.configXml";
    @PropDoc(group = "j2cache", value = "L1缓存提供者,可以有:none,ehcache,ehcache3,caffeine,redis", defaultValue = "")
    public static final String PROP_L1_PROVIDER_CLASS = PRE + "L1.provider_class";
    @PropDoc(group = "j2cache", value = "L2缓存提供者,可以有:none,ehcache,ehcache3,caffeine,redis", defaultValue = "")
    public static final String PROP_L2_PROVIDER_CLASS = PRE + "L2.provider_class";
    @PropDoc(group = "j2cache", value = "序列化类型,可以有:fst(fast-serialization),kyro(kyro),java(java standard)", defaultValue = "")
    public static final String PROP_SERIALIZATION = PRE + "serialization";
    @PropDoc(group = "j2cache", value = "Ehcache的配置文件路径", defaultValue = "")
    public static final String PROP_EHCACHE_CONFIGXML = PRE + "ehcache.configXml";
    @PropDoc(group = "j2cache", value = "Ehcache3的配置文件路径", defaultValue = "")
    public static final String PROP_EHCACHE3_CONFIGXML = PRE + "ehcache3.configXml";
    @PropDoc(group = "j2cache", value = "Ehcache3的缓存文件大小", defaultValue = "")
    public static final String PROP_EHCACHE3_DEFAULTHEAPSIZE = PRE + "ehcache3.defaultHeapSize";
    @PropDoc(group = "j2cache", value = "Caffeine的配置文件", defaultValue = "")
    public static final String PROP_CAFFEINE_PROPERTIES= PRE + "caffeine.properties";
    @PropDoc(group = "j2cache", value = "Caffeine的配置,参考:1000,1h ", defaultValue = "")
    public static final String PROP_CAFFEINE_REGION_DEFAULT= PRE + "caffeine.region.default";
    @PropDoc(group = "j2cache", value = "Redis的mode,可以有:single(single redis server),sentinel(master-slaves servers),cluster(cluster servers),sharded(sharded servers)", defaultValue = "")
    public static final String PROP_REDIS_MODE= PRE + "redis.mode";
    @PropDoc(group = "j2cache", value = "Redis的存储mode,可以有:generic,hash", defaultValue = "")
    public static final String PROP_REDIS_STORAGE= PRE + "redis.storage";
    @PropDoc(group = "j2cache", value = "Redis的Channel的名字", defaultValue = "")
    public static final String PROP_REDIS_CHANNEL= PRE + "redis.channel";
    @PropDoc(group = "j2cache", value = "Redis的Channel的主机", defaultValue = "")
    public static final String PROP_REDIS_CHANNEL_HOST= PRE + "redis.channel.host";
    @PropDoc(group = "j2cache", value = "Redis主机名(包含端口)", defaultValue = "")
    public static final String PROP_REDIS_HOSTS = PRE + "redis.hosts";
    @PropDoc(group = "j2cache", value = "Redis连接超时时间", defaultValue = "")
    public static final String PROP_REDIS_TIMEOUT = PRE + "redis.timeout";
    @PropDoc(group = "j2cache", value = "Redis连接密码", defaultValue = "")
    public static final String PROP_REDIS_PASSWORD = PRE + "redis.password";
    @PropDoc(group = "j2cache", value = "Redis的集群名称", defaultValue = "")
    public static final String PROP_REDIS_CLUSTER_NAME= PRE + "redis.cluster_name";
    @PropDoc(group = "j2cache", value = "Redis的命名空间", defaultValue = "j2cache")
    public static final String PROP_REDIS_NAMESPACE= PRE + "redis.namespace";
    @PropDoc(group = "j2cache", value = "Redis的可用数据库数", defaultValue = "")
    public static final String PROP_REDIS_DATABASE= PRE + "redis.database";
    @PropDoc(group = "j2cache", value = "最大连接数", defaultValue = "")
    public static final String PROP_REDIS_MAXTOTAL= PRE + "redis.maxTotal";
    @PropDoc(group = "j2cache", value = "最大空闲连接数", defaultValue = "")
    public static final String PROP_REDIS_MAXIDLE= PRE + "redis.maxIdle";
    @PropDoc(group = "j2cache", value = "获取连接时的最大等待毫秒数", defaultValue = "")
    public static final String PROP_REDIS_MAXWAITMILLIS= PRE + "redis.maxWaitMillis";
    @PropDoc(group = "j2cache", value = "逐出连接的最小空闲时间", defaultValue = "")
    public static final String PROP_REDIS_MINEVICTABLEIDLETIMEMILLIS= PRE + "redis.minEvictableIdleTimeMillis";
    @PropDoc(group = "j2cache", value = "最小空闲连接数", defaultValue = "")
    public static final String PROP_REDIS_MINIDLE= PRE + "redis.minIdle";
    @PropDoc(group = "j2cache", value = "每次逐出检查时,逐出的最大数目", defaultValue = "")
    public static final String PROP_REDIS_NUMTESTSPEREVICTIONRUN= PRE + "redis.numTestsPerEvictionRun";
    @PropDoc(group = "j2cache", value = "是否启用后进先出", defaultValue = "")
    public static final String PROP_REDIS_LIFO= PRE + "redis.lifo";
    @PropDoc(group = "j2cache", value = "对象空闲多久后逐出", defaultValue = "")
    public static final String PROP_REDIS_SOFTMINEVICTABLEIDLETIMEMILLIS= PRE + "redis.softMinEvictableIdleTimeMillis";
    @PropDoc(group = "j2cache", value = "在获取连接的时候是否检查有效性", defaultValue = "")
    public static final String PROP_REDIS_TESTONBORROW= PRE + "redis.testOnBorrow";
    @PropDoc(group = "j2cache", value = "在return给pool时，是否提前进行validate操作；", defaultValue = "")
    public static final String PROP_REDIS_TESTONRETURN= PRE + "redis.testOnReturn";
    @PropDoc(group = "j2cache", value = "在空闲时是否检查有效性", defaultValue = "")
    public static final String PROP_REDIS_TESTWHILEIDLE= PRE + "redis.testWhileIdle";
    @PropDoc(group = "j2cache", value = "逐出扫描的时间间隔(毫秒)", defaultValue = "")
    public static final String PROP_REDIS_TIMEBETWEENEVICTIONRUNSMILLIS= PRE + "redis.timeBetweenEvictionRunsMillis";
    @PropDoc(group = "j2cache", value = "连接耗尽时是否阻塞,如false则报异常,如ture则阻塞直到超时", defaultValue = "")
    public static final String PROP_REDIS_blockWhenExhausted= PRE + "redis.blockWhenExhausted";

    @IocBean
    public CacheChannel getCacheChannel() throws IOException {
        Properties nb_broadcastProperties = new Properties();
        Properties nb_l1CacheProperties = new Properties();
        Properties nb_l2CacheProperties = new Properties();

        String nb_serialization= Strings.trim(conf.get(PRE+"serialization"));
        String nb_broadcast = Strings.trim(conf.get(PRE+"broadcast"));
        String nb_l1CacheName = Strings.trim(conf.get(PRE+"L1.provider_class"));
        String nb_l2CacheName = Strings.trim(conf.get(PRE+"L2.provider_class"));

        conf.keys().forEach((k) -> {
            if(k.startsWith(PRE+nb_broadcast + ".")) {
                nb_broadcastProperties.setProperty(k.substring(PRE.length()+((nb_broadcast + ".").length())), conf.get(k));
            }
            if(k.startsWith(PRE+nb_l1CacheName + "."))
                nb_l1CacheProperties.setProperty(k.substring(PRE.length()+((nb_l1CacheName + ".").length())),conf.get(k));
            if(k.startsWith(PRE+nb_l2CacheName + "."))
                nb_l2CacheProperties.setProperty(k.substring(PRE.length()+((nb_l2CacheName + ".").length())), conf.get(k));
        });

        J2CacheConfig j2CacheConfig = new J2CacheConfig();
        j2CacheConfig.setBroadcast(nb_broadcast);
        j2CacheConfig.setBroadcastProperties(nb_broadcastProperties);
        j2CacheConfig.setL1CacheName(nb_l1CacheName);
        j2CacheConfig.setL1CacheProperties(nb_l1CacheProperties);
        j2CacheConfig.setSerialization(nb_serialization);
        j2CacheConfig.setL2CacheName(nb_l2CacheName);
        j2CacheConfig.setL2CacheProperties(nb_l2CacheProperties);

        return J2CacheBuilder.init(j2CacheConfig).getChannel();
    }
}
