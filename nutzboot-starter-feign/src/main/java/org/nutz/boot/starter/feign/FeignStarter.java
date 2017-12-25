package org.nutz.boot.starter.feign;

import java.lang.reflect.Field;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.feign.annotation.FeignInject;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocEventListener;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import feign.Feign;
import feign.Logger.Level;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;

@IocBean
public class FeignStarter implements IocEventListener {

    protected static String PRE = "feign.";
    @PropDoc(value = "日志级别", defaultValue = "basic", possible= {"none", "basic", "headers", "full"})
    public static final String PROP_LOGLEVEL = PRE + "logLevel";
    @PropDoc(value = "Api Base URL", defaultValue = "http://127.0.0.1:8080")
    public static final String PROP_URL = PRE + "url";

    @Inject("refer:$ioc")
    protected Ioc ioc;

    @Inject
    protected PropertiesProxy conf;

    public Object afterBorn(Object obj, String beanName) {
        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                FeignInject fc = field.getAnnotation(FeignInject.class);
                if (fc == null)
                    continue;
                Feign.Builder builder = Feign.builder();
                switch (fc.encoder()) {
                case "jackson":
                    builder.encoder(new JacksonEncoder());
                    break;
                default:
                    break;
                }
                switch (fc.decoder()) {
                case "jackson":
                    builder.decoder(new JacksonDecoder());
                    break;
                default:
                    break;
                }
                builder.logger(new Slf4jLogger());
                builder.logLevel(Level.valueOf(conf.get(PROP_LOGLEVEL, "BASIC").toUpperCase()));
                Object t = builder.target(field.getType(), Strings.sBlank(conf.get(PROP_URL), "http://127.0.0.1:8080"));
                field.setAccessible(true);
                field.set(obj, t);
            }
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    public Object afterCreate(Object obj, String beanName) {
        return obj;
    }

    public int getOrder() {
        return 0;
    }

}
