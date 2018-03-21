package org.nutz.boot.starter.ssdb;

import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ssdb4j.SSDBs;
import org.nutz.ssdb4j.spi.SSDB;

/**
 * 基于SSDB4j
 * https://gitee.com/wendal/ssdb4j
 * https://github.com/nutzam/ssdb4j
 * 针对SSDB
 * http://ssdb.io/
 * 用于NutzBoot
 * https://gitee.com/nutz/nutzboot
 * https://github.com/nutzam/nutzboot
 * 的Starter
 * <p>
 * Starter for SSDB base on ssdb4j
 *
 * @author DaoKun
 * @date 2018.03.21
 */
@IocBean
public class SsdbStarter {

    protected static final String PRE = "ssdb.";

    @PropDoc(value = "SSDB的服务地址", defaultValue = "127.0.0.1")
    public static final String PROP_HOST = PRE + "host";
    @PropDoc(value = "SSDB的服务端口", defaultValue = "8888")
    public static final String PROP_PORT = PRE + "port";
    @PropDoc(value = "SSDB的服务超时时间", defaultValue = "2000")
    public static final String PROP_TIMEOUT = PRE + "timeout";
    @PropDoc(value = "SSDB的最大连接数", defaultValue = "10")
    public static final String PROP_MAXACTIVE = PRE + "maxActive";
    @PropDoc(value = "SSDB是否在空闲时检测链接存活", defaultValue = "true")
    public static final String PROP_TESTWHILEIDLE = PRE + "testWhileIdle";
    @Inject
    protected PropertiesProxy conf;

    @IocBean(name = "ssdb")
    public SSDB makeSSDB() {
        String host = SSDBs.DEFAULT_HOST;
        int port = SSDBs.DEFAULT_PORT;
        int timeout = SSDBs.DEFAULT_TIMEOUT;
        if (conf.has(PROP_HOST)) {
            host = conf.get(PROP_HOST);
        }
        if (conf.has(PROP_PORT)) {
            port = conf.getInt(PROP_PORT);
        }
        if (conf.has(PROP_TIMEOUT)) {
            timeout = conf.getInt(PROP_TIMEOUT);
        }
        Config config = new Config();
        if (conf.has(PROP_MAXACTIVE)) {
            config.maxActive = conf.getInt(PROP_MAXACTIVE);
        } else {
            config.maxActive = 10;
        }
        if (conf.has(PROP_TESTWHILEIDLE)) {
            config.testWhileIdle = conf.getBoolean(PROP_TESTWHILEIDLE);
        } else {
            config.testWhileIdle = true;
        }
        return SSDBs.pool(host, port, timeout, config);
    }
}
