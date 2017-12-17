package org.nutz.boot.starter.mongodb;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mongo.ZMoDB;
import org.nutz.mongo.ZMongo;

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@IocBean
public class NutMongoDbStarter {

	private Log logger = Logs.get();
	
	protected static String PRE = "mongo.";
	@PropDoc(group = "mongo", value = "基于Mongo URI创建链接")
	public static final String PROP_URI = PRE + "uri";
	@PropDoc(group = "mongo", value = "数据库ip", defaultValue = "127.0.0.1")
	public static final String PROP_IP = PRE + "ip";
	@PropDoc(group = "mongo", value = "数据库port", defaultValue = "27017")
	public static final String PROP_PORT = PRE + "port";
	@PropDoc(group = "mongo", value = "数据库用户名")
	public static final String PROP_USERNAME = PRE + "username";
	@PropDoc(group = "mongo", value = "数据库密码")
	public static final String PROP_PASSWORD = PRE + "password";
	@PropDoc(group = "mongo", value = "数据库名称", need = true)
	public static final String PROP_DBNAME = PRE + "dbname";

	// 连接池配置
	@PropDoc(group = "mongo", value = "最小连接数", defaultValue = "0")
	public static final String PROP_MIN_CONNECTIONS_PER_HOST = PRE + "minConnectionsPerHost";
	@PropDoc(group = "mongo", value = "最大连接数", defaultValue = "100")
	public static final String PROP_MAX_CONNECTIONS_PER_HOST = PRE + "maxConnectionsPerHost";

	@PropDoc(group = "mongo", value = "线程队列数", defaultValue = "5")
	public static final String PROP_THREADS = PRE + "threadsAllowedToBlockForConnectionMultiplier";

	@PropDoc(group = "mongo", value = "最大等待可用连接时间", defaultValue = "1000*60*2")
	public static final String PROP_MAX_WAIT_TIME = PRE + "maxWaitTime";
	@PropDoc(group = "mongo", value = "连接超时时间", defaultValue = "1000*10")
	public static final String PROP_CONNECT_TIMEOUT = PRE + "connectTimeout";
	@PropDoc(group = "mongo", value = "套接字超时时间", defaultValue = "0")
	public static final String PROP_SOCKET_TIMEOUT = PRE + "socketTimeout";

	@Inject
	protected PropertiesProxy conf;

	@IocBean(name = "zmongo")
	public ZMongo getZMongo() {
		// 优先使用uri创建连接, uri含options故直接连接
		if (conf.containsKey(PROP_URI)) {
			return ZMongo.uri(conf.get(PROP_URI));
		}

		// 连接选项控制
		MongoClientOptions.Builder builder = MongoClientOptions.builder();
		builder.minConnectionsPerHost(conf.getInt(PROP_MIN_CONNECTIONS_PER_HOST, 0));
		builder.connectionsPerHost(conf.getInt(PROP_MAX_CONNECTIONS_PER_HOST, 100));
		builder.threadsAllowedToBlockForConnectionMultiplier(conf.getInt(PROP_THREADS, 5));
		builder.maxWaitTime(conf.getInt(PROP_MAX_WAIT_TIME, 1000 * 60 * 2));
		builder.connectTimeout(conf.getInt(PROP_CONNECT_TIMEOUT, 1000 * 10));
		builder.socketTimeout(conf.getInt(PROP_SOCKET_TIMEOUT, 0));
		
		MongoClientOptions options = builder.build();
		logger.info(options); // 打印现有的参数信息，便于调优

		ServerAddress address = new ServerAddress(conf.get(PROP_IP), conf.getInt(PROP_PORT));
		if (conf.containsKey(PROP_USERNAME)) {
			MongoCredential cred = MongoCredential.createScramSha1Credential(conf.get(PROP_USERNAME), null,
					conf.get(PROP_PASSWORD, "").toCharArray());
			return ZMongo.me(address, cred, options);
		}
		return ZMongo.me(address, null, options);
	}

	@IocBean(name = "zmodb")
	public ZMoDB getZMoDB(@Inject ZMongo zmongo) {
		String dbname = conf.get(PROP_DBNAME);
		if (Strings.isBlank(dbname))
			throw Lang.makeThrow("not config mongo.dbname!");

		return zmongo.db(dbname);
	}

}
