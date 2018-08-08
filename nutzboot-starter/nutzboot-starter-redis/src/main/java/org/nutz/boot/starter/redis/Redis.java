package org.nutz.boot.starter.redis;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Lang;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Map;

/**
 * redis操作类
 *
 * @author wentao
 * @email wentao0291@gmail.com
 * @date 2018-08-08 21:47
 */
public class Redis {
    private static final String host_prefix = "redis.%s.host";
    private static final String pass_prefix = "redis.%s.password";
    private static final String max_total_prefix = "redis.%s.pool.max.total";
    private static final String max_idle_prefix = "redis.%s.pool.max.idle";
    private static final String max_wait_millis_prefix = "redis.%s.pool.max.wait.millis";
    private static final String test_on_borrow_prefix = "redis.%s.pool.test.on.borrow";
    private JedisPool defualtPool;
    private JedisPool masterPool;
    private JedisPool slavePool;
    private boolean isMasterSlave;
    public Redis(String redisName, PropertiesProxy conf) {
        /**
         * redis.local.host=localhost:6379|localhost:6380
         * redis.local.password=123123
         * redis.local.pool.max.total=50
         * redis.local.pool.max.idle=5
         * redis.local.pool.max.wait.millis=100000
         * redis.local.pool.test.on.borrow=true
         */
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(conf.getInt(String.format(max_total_prefix, redisName), 50));
        config.setMaxIdle(conf.getInt(String.format(max_idle_prefix, redisName), 5));
        config.setMaxWaitMillis(conf.getInt(String.format(max_wait_millis_prefix, redisName), 100000));
        config.setTestOnBorrow(conf.getBoolean(String.format(test_on_borrow_prefix, redisName), true));
        // 读取IP集
        String hosts = conf.get(String.format(host_prefix, redisName));
        // 读取密码
        String password = conf.get(String.format(pass_prefix, redisName));
        // 如果hosts长度为0，则跳过处理
        if(hosts.length() > 0) {
            // 如果是主从
            this.isMasterSlave = hosts.contains("|"); //如果host配置带有"|"符号, 则视为主从结构
            if (this.isMasterSlave) {
                // 切分地址
                String[] hostArray = hosts.split("\\|");
                // 解析主redis信息
                String masterHost = hosts.split("\\|")[0].split(":")[0];
                int masterPort = Integer.parseInt(hosts.split("\\|")[0].split(":")[1]);
                // 解析从redis信息
                String slaveHost = hosts.split("\\|")[1].split(":")[0];
                int slavePort = Integer.parseInt(hosts.split("\\|")[1].split(":")[1]);
                // 配置主redis池
                if (password.isEmpty()) {
                    this.masterPool = new JedisPool(config, masterHost, masterPort);
                } else {
                    this.masterPool = new JedisPool(config, masterHost, masterPort, 2000, password);
                }
                // 配置从redis池
                if (password.isEmpty()) {
                    this.slavePool = new JedisPool(config, slaveHost, slavePort);
                } else {
                    this.slavePool = new JedisPool(config, slaveHost, slavePort, 2000, password);
                }
            } else {
                String host = hosts.split(":")[0];
                int port = Integer.parseInt(hosts.split(":")[1]);
                if (password.isEmpty()) {
                    this.defualtPool = new JedisPool(config, host, port);
                } else {
                    this.defualtPool = new JedisPool(config, host, port, 2000, password);
                }
            }
        }
    }
    private Jedis getMasterRedis() throws RuntimeException {
        if(this.masterPool == null && this.defualtPool == null) {
            throw Lang.makeThrow("未连接到Redis，请检查配置项");
        }

        if(this.isMasterSlave) {
            return this.masterPool.getResource();
        } else {
            return this.defualtPool.getResource();
        }
    }
    private Jedis getSlaveRedis() throws RuntimeException {
        if(this.slavePool == null && this.defualtPool == null) {
            throw Lang.makeThrow("未连接到Redis，请检查配置项");
        }

        if(this.isMasterSlave) {
            return this.slavePool.getResource();
        } else {
            return this.defualtPool.getResource();
        }
    }
    public String get(String key) {
        Jedis redis = null;
        try {
            redis = getSlaveRedis();
            return redis.get(key);
        } finally {
            redis.close();
        }
    }
    public String set(String key, String value) {
        Jedis redis = null;
        try {
            redis = getMasterRedis();
            return redis.set(key, value);
        } finally {
            redis.close();
        }
    }
    public Long dbsize() {
        Jedis redis = null;
        try {
            redis = getSlaveRedis();
            return redis.dbSize();
        } finally {
            redis.close();
        }
    }
    public Long del(String... keys) {
        Jedis redis = null;
        try {
            redis = getMasterRedis();
            return redis.del(keys);
        } finally {
            redis.close();
        }
    }
    public Long hset(String key, String field, String value) {
        Jedis redis = null;
        try {
            redis = getMasterRedis();
            return redis.hset(key, field, value);
        } finally {
            redis.close();
        }
    }
    public String hget(String key, String field) {
        Jedis redis = null;
        try {
            redis = getSlaveRedis();
            return redis.hget(key, field);
        } finally {
            redis.close();
        }
    }
    public Map<String, String> hgetall(String key) {
        Jedis redis = null;
        try {
            redis = getSlaveRedis();
            return redis.hgetAll(key);
        } finally {
            redis.close();
        }
    }
    public Long hdel(String key, String... fields) {
        Jedis redis = null;
        try {
            redis = getMasterRedis();
            return redis.hdel(key, fields);
        } finally {
            redis.close();
        }
    }
    public Boolean exists(String key) {
        Jedis redis = null;
        try {
            redis = getSlaveRedis();
            return redis.exists(key);
        } finally {
            redis.close();
        }
    }
    public String lpop(String key) {
        Jedis redis = null;
        try {
            redis = getMasterRedis();
            return redis.lpop(key);
        } finally {
            redis.close();
        }
    }
    public String rpop(String key) {
        Jedis redis = null;
        try {
            redis = getMasterRedis();
            return redis.rpop(key);
        } finally {
            redis.close();
        }
    }
    public List<String> blpop(int timeout, String key) {
        Jedis redis = null;
        try {
            redis = getMasterRedis();
            return redis.blpop(timeout, key);
        } finally {
            redis.close();
        }
    }
    public List<String> brpop(int timeout, String key) {
        Jedis redis = null;
        try {
            redis = getMasterRedis();
            return redis.brpop(timeout, key);
        } finally {
            redis.close();
        }
    }
    public Long lpush(String key, String... values) {
        Jedis redis = null;
        try {
            redis = getMasterRedis();
            return redis.lpush(key, values);
        } finally {
            redis.close();
        }
    }
    public Long rpush(String key, String... values) {
        Jedis redis = null;
        try {
            redis = getMasterRedis();
            return redis.rpush(key, values);
        } finally {
            redis.close();
        }
    }
    public Long llen(String key) {
        Jedis redis = null;
        try {
            redis = getSlaveRedis();
            return redis.llen(key);
        } finally {
            redis.close();
        }
    }
    public Long i(String key) {
        Jedis redis = null;
        try {
            redis = getMasterRedis();
            return redis.incr(key);
        } finally {
            redis.close();
        }
    }
}
