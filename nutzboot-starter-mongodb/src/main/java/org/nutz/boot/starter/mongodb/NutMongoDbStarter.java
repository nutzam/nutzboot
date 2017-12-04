package org.nutz.boot.starter.mongodb;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mongo.ZMoDB;
import org.nutz.mongo.ZMongo;

@IocBean
public class NutMongoDbStarter {

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

	@Inject
	protected PropertiesProxy conf;

	@IocBean(name = "zmongo")
	public ZMongo getZMongo() {
		// 优先使用uri创建连接
		if (conf.containsKey(PROP_URI)) {
			return ZMongo.uri(conf.get(PROP_URI));
		}
		if (conf.containsKey(PROP_USERNAME)) {
			return ZMongo.me(conf.get(PROP_USERNAME), conf.get(PROP_PASSWORD), conf.get(PROP_IP, "127.0.0.1"), conf.getInt(PROP_PORT, 27017));
		}
		return ZMongo.me(conf.get(PROP_IP, "127.0.0.1"), conf.getInt(PROP_PORT, 27017));
	}

	@IocBean(name = "zmodb")
	public ZMoDB getZMoDB(@Inject ZMongo zmongo) {
		String dbname = conf.get(PROP_DBNAME);
		if (Strings.isBlank(dbname))
			throw Lang.makeThrow("not config mongo.dbname!");

		return zmongo.db(dbname);
	}

}
