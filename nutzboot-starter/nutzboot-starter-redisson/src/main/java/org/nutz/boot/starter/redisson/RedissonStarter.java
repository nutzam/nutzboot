package org.nutz.boot.starter.redisson;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.ioc.IocLoaderProvider;
import org.nutz.integration.redisson.RedissonIocLoader;
import org.nutz.ioc.IocLoader;

public class RedissonStarter implements IocLoaderProvider {

    protected static String PRE = "redisson.";

    @PropDoc(value = "使用json配置redisson,一般不选,属于高级配置")
    public static final String PROP_HOST = PRE + "config.fromJson";
    @PropDoc(value = "使用yaml配置redisson,一般不选,属于高级配置")
    public static final String PROP_PORT = PRE + "config.fromYaml";

    @PropDoc(value = "redisson模式,默认单机", defaultValue = "single", possible = {"single", "masterslave", "cluster", "replicated", "sentinel"})
    public static final String PROP_TIMEOUT = PRE + "mode";
    @PropDoc(value = "redis服务器密码")
    public static final String PROP_PASSWORD = PRE + "password";
    @PropDoc(value = "redisson序列化器", defaultValue = "fst", possible = {"fst", "kryo", "lz4", "jdk", "snappy", "snappy2", "json-jackson", "msgpack-jackson", "cbor-jackson", "smile-jackson"})
    public static final String PROP_CODEC = PRE + "codec";

    @PropDoc(value = "redisson通用配置项,对应org.redisson.config.Config类")
    public static final String PROP_CONFIG_COMMON = PRE + "*";

    @PropDoc(value = "redisson单机模式配置项, 对应org.redisson.config.SingleServerConfig")
    public static final String PROP_SINGLE_CONFIG = PRE + "single.*";
    @PropDoc(value = "redisson单机地址", defaultValue = "redis://127.0.0.1:6379")
    public static final String PROP_SINGLE_ADDRESS = PRE + "single.address";

    @PropDoc(value = "redisson单机模式配置项, 对应org.redisson.config.ClusterServersConfig")
    public static final String PROP_CLUSTER_CONFIG = PRE + "cluster.*";
    @PropDoc(value = "redisson集群模式节点地址,逗号分隔")
    public static final String PROP_CLUSTER_ADDRESS = PRE + "cluster.nodeAddress";

    public IocLoader getIocLoader() {
        return new RedissonIocLoader();
    }
}
