package org.nutz.boot.starter.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mongo.ZMoDB;
import org.nutz.mongo.ZMongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

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
    @PropDoc(group = "mongo", value = "数据库用户所在的源数据库")
    public static final String PROP_SOURCE = PRE + "source";
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

    @PropDoc(group = "mongo", value = "写入策略")
    public static final String PROP_WRITECONCERN = PRE + "writeConcern";

    @PropDoc(group = "mongo", value = "读取策略")
    public static final String PROP_READCONCERN = PRE + "readConcern";

    @PropDoc(group = "mongo", value = "读取优先策略")
    public static final String PROP_READPREFERENCE = PRE + "readPreference";

    @PropDoc(group = "mongo", value = "服务器列表,逗号分隔")
    public static final String PROP_HOSTS = PRE + "hosts";

    @Inject
    protected PropertiesProxy conf;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    /**
     * 获取Mongodb的配置信息
     */
    @IocBean(name = "mongoClientOptions")
    public MongoClientOptions createMongoClientOptions() {
        // 连接选项控制
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        builder.minConnectionsPerHost(conf.getInt(PROP_MIN_CONNECTIONS_PER_HOST, 0));
        builder.connectionsPerHost(conf.getInt(PROP_MAX_CONNECTIONS_PER_HOST, 100));
        builder.threadsAllowedToBlockForConnectionMultiplier(conf.getInt(PROP_THREADS, 5));
        builder.maxWaitTime(conf.getInt(PROP_MAX_WAIT_TIME, 1000 * 60 * 2));
        builder.connectTimeout(conf.getInt(PROP_CONNECT_TIMEOUT, 1000 * 10));
        builder.socketTimeout(conf.getInt(PROP_SOCKET_TIMEOUT, 0));
        // 写入的策略
        if (conf.has(PROP_WRITECONCERN)) {
            builder.writeConcern(WriteConcern.valueOf(conf.get(PROP_WRITECONCERN).trim().toUpperCase()));
        }
        // 读取的策略
        if (conf.has(PROP_READCONCERN)) {
            switch (conf.get(PROP_READCONCERN).trim().toUpperCase()) {
            case "DEFAULT":
                builder.readConcern(ReadConcern.DEFAULT);
                break;
            case "LOCAL":
                builder.readConcern(ReadConcern.LOCAL);
                break;
            case "MAJORITY":
                builder.readConcern(ReadConcern.MAJORITY);
                break;
            case "LINEARIZABLE":
                builder.readConcern(ReadConcern.LINEARIZABLE);
                break;
            default:
                break;
            }
        }
        // 读取的优先级策略
        if (conf.has(PROP_READPREFERENCE)) {
            switch (conf.get(PROP_READPREFERENCE)) {
            case "primary":
                builder.readPreference(ReadPreference.primary());
                break;
            case "primaryPreferred":
                builder.readPreference(ReadPreference.primaryPreferred());
                break;
            case "secondary":
                builder.readPreference(ReadPreference.secondary());
                break;
            case "secondaryPreferred":
                builder.readPreference(ReadPreference.secondaryPreferred());
                break;
            case "nearest":
                builder.readPreference(ReadPreference.nearest());
                break;
            default:
                break;
            }
        }
        // 完工,再见...
        MongoClientOptions options = builder.build();
        logger.info(options); // 打印现有的参数信息，便于调优
        return options;
    }

    /**
     * 获取mongodb服务器列表
     */
    @IocBean(name = "mongodbServerAddressList")
    public List<ServerAddress> getServerAddressList() {
        // 看看是不是配置了集群
        List<ServerAddress> addrs = new ArrayList<>();
        if (conf.has(PROP_HOSTS)) {
            String[] address = Strings.splitIgnoreBlank(conf.get(PROP_HOSTS));
            for (String addr : address) {
                String[] tmp = addr.split(":");
                addrs.add(new ServerAddress(tmp[0], Integer.parseInt(tmp[1])));
            }
        } else {
            addrs.add(new ServerAddress(conf.get(PROP_IP, ServerAddress.defaultHost()), conf.getInt(PROP_PORT, ServerAddress.defaultPort())));
        }
        return addrs;
    }

    /**
     * 获取Mongo的认证信息
     * 
     * @return
     */
    @IocBean(name = "mongodbCredentialList")
    public List<MongoCredential> getCredentialList() {
        List<MongoCredential> credentials = new ArrayList<>();
        if (conf.containsKey(PROP_USERNAME)) {
            credentials.add(MongoCredential.createScramSha1Credential(conf.get(PROP_USERNAME), conf.get(PROP_SOURCE), conf.get(PROP_PASSWORD, "").toCharArray()));
        }
        return credentials;
    }

    /**
     * 获取MongoClient实例
     */
    @SuppressWarnings("unchecked")
    @IocBean(name = "mongoClient")
    public MongoClient createMongoClient() {
        // 如果配的是URL,直接返回了
        if (conf.containsKey(PROP_URI)) {
            return new MongoClient(new MongoClientURI(conf.get(PROP_URI)));
        }
        return new MongoClient(ioc.get(List.class, "mongodbServerAddressList"), ioc.get(List.class, "mongodbCredentialList"), ioc.get(MongoClientOptions.class));
    }

    @IocBean(name = "zmongo")
    public ZMongo getZMongo() {
        return ZMongo.me(ioc.get(MongoClient.class));
    }

    @IocBean(name = "zmodb")
    public ZMoDB getZMoDB(@Inject ZMongo zmongo) {
        String dbname = conf.get(PROP_DBNAME);
        if (Strings.isBlank(dbname))
            throw Lang.makeThrow("not config mongo.dbname!");

        return zmongo.db(dbname);
    }

}
