package org.nutz.boot.starter.redis;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.integration.jedis.JedisIocLoader;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.impl.PropertiesProxy;

//@IocBean
public class JedisStarter implements IocLoaderProvider {
	
	//@Inject
	protected PropertiesProxy conf;

    protected static String PRE = "redis.";
    @PropDoc(value="redis服务器ip或域名", defaultValue="127.0.0.1")
    public static final String PROP_HOST = PRE + "host";
    @PropDoc(value="redis服务器端口", defaultValue="6379")
    public static final String PROP_PORT = PRE + "port";
    @PropDoc(value="redis读写超时", defaultValue="2000")
    public static final String PROP_TIMEOUT = PRE + "timeout";
    @PropDoc(value="redis密码")
    public static final String PROP_PASSWORD = PRE + "password";
    @PropDoc(value="redis数据库序号", defaultValue="0")
    public static final String PROP_DATABASE = PRE + "database";
    @PropDoc(value="redis集群节点列表")
    public static final String PROP_NODES = PRE + "nodes";
    @PropDoc(value="redis写超时", defaultValue="0")
    public static final String PROP_SO_TIMEOUT = PRE + "soTimeout";
    @PropDoc(value="redis集群最大重定向次数", defaultValue="10")
    public static final String PROP_MAX_REDIR = PRE + "max_redir";
    
    public IocLoader getIocLoader() {
//    	if (Strings.isBlank(conf.get("redis.password"))) {
//    		conf.remove("redis.password");
//    	}
    	return new JedisIocLoader();
    }
}
