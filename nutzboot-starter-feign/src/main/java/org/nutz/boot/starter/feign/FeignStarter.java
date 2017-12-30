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
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxb.JAXBContextFactory;
import feign.jaxb.JAXBDecoder;
import feign.jaxb.JAXBEncoder;
import feign.jaxrs.JAXRSContract;
import feign.okhttp.OkHttpClient;
import feign.ribbon.RibbonClient;
import feign.slf4j.Slf4jLogger;

@IocBean
public class FeignStarter implements IocEventListener {

    protected static String PRE = "feign.";

    @PropDoc(value = "日志级别", defaultValue = "basic", possible = {"none", "basic", "headers", "full"})
    public static final String PROP_LOGLEVEL = PRE + "logLevel";

    @PropDoc(value = "Api Base URL", defaultValue = "http://127.0.0.1:8080")
    public static final String PROP_URL = PRE + "url";

    @PropDoc(value = "客户端实现类", defaultValue = "jdk", possible = {"jdk", "httpclient", "okhttp", "ribbon"})
    public static final String PROP_CLIENT = PRE + "client";

    @PropDoc(value = "WebService的schema地址")
    public static final String PROP_SCHEMA = PRE + "schema";

    @PropDoc(value = "默认编码器", possible= {"raw", "nutzjson", "jackson", "gson", "jaxb", "jaxrs"})
    public static final String PROP_ENCODER = PRE + "encoder";

    @PropDoc(value = "默认解码器", possible= {"raw", "nutzjson", "jackson", "gson", "jaxb", "jaxrs"})
    public static final String PROP_DECODER = PRE + "decoder";

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
                switch (Strings.sBlank(fc.encoder(), conf.get(PROP_ENCODER, ""))) {
                case "":
                case "raw":
                    break;
                case "nutzjson":
                    builder.encoder(new NutzJsonEncoder());
                    break;
                case "jackson":
                    builder.encoder(new JacksonEncoder());
                    break;
                case "gson":
                    builder.encoder(new GsonEncoder());
                    break;
                case "jaxb":
                    JAXBContextFactory jaxbFactory = new JAXBContextFactory.Builder().withMarshallerJAXBEncoding("UTF-8").withMarshallerSchemaLocation(fc.schema()).build();
                    builder.encoder(new JAXBEncoder(jaxbFactory));
                    break;
                case "jaxrs":
                    builder.contract(new JAXRSContract());
                    break;
                default:
                    break;
                }
                switch (Strings.sBlank(fc.decoder(), conf.get(PROP_DECODER, ""))) {
                case "":
                case "raw":
                    break;
                case "nutzjson":
                    builder.decoder(new NutzJsonDecoder());
                    break;
                case "jackson":
                    builder.decoder(new JacksonDecoder());
                    break;
                case "gson":
                    builder.decoder(new GsonDecoder());
                    break;
                case "jaxb":
                    JAXBContextFactory jaxbFactory = new JAXBContextFactory.Builder().withMarshallerJAXBEncoding("UTF-8")
                                                                                     .withMarshallerSchemaLocation(Strings.sBlank(fc.schema(), conf.get(PROP_SCHEMA)))
                                                                                     .build();
                    builder.decoder(new JAXBDecoder(jaxbFactory));
                    break;
                case "jaxrs":
                    builder.contract(new JAXRSContract());
                    break;
                default:
                    break;
                }
                switch (Strings.sBlank(fc.client(), conf.get(PROP_CLIENT, "jdk"))) {
                case "jdk":
                    // nop
                    break;
                case "okhttp":
                    builder.client(new OkHttpClient());
                    break;
                case "httpclient":
                    builder.client(new ApacheHttpClient());
                    break;
                case "ribbon":
                    builder.client(RibbonClient.create());
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
