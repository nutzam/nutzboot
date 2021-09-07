package org.nutz.boot.starter.mongodb.plus;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.boot.annotation.PropDoc;
/**
 * @author wizzer@qq.com
 */
@IocBean
public class ZMongoStarter {

    protected static String PRE = "mongodb.";

    @PropDoc(group = "mongodb", value = "基于Mongo URI创建链接")
    public static final String PROP_URI = PRE + "url";

    @Inject
    protected PropertiesProxy conf;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    @IocBean(name = "zMongoClient")
    public ZMongoClient createZMongoClient(){
        return ZMongoClient.me(conf.get(PROP_URI));
    }

    @IocBean(name = "zMongoDatabase")
    public ZMongoDatabase getZMongoDatabase(@Inject ZMongoClient zMongoClient){
        return zMongoClient.db();
    }
}
