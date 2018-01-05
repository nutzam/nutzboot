package org.nutz.boot.starter.feign;

import java.lang.reflect.Field;
import java.util.List;

import javax.inject.Provider;

import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.feign.annotation.FeignInject;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocEventListener;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;

import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.discovery.EurekaClient;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.LoadBalancerBuilder;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.ServerList;
import com.netflix.loadbalancer.ServerListFilter;
import com.netflix.loadbalancer.ServerListUpdater;
import com.netflix.loadbalancer.ZoneAffinityServerListFilter;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import com.netflix.niws.loadbalancer.EurekaNotificationServerListUpdater;

import feign.Client;
import feign.Feign;
import feign.Logger;
import feign.Logger.Level;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.httpclient.ApacheHttpClient;
import feign.hystrix.HystrixFeign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxb.JAXBContextFactory;
import feign.jaxb.JAXBDecoder;
import feign.jaxb.JAXBEncoder;
import feign.okhttp.OkHttpClient;
import feign.ribbon.LBClient;
import feign.ribbon.LBClientFactory;
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

    @PropDoc(value = "默认编码器", possible = {"raw", "nutzjson", "jackson", "gson", "jaxb", "jaxrs"})
    public static final String PROP_ENCODER = PRE + "encoder";

    @PropDoc(value = "默认解码器", possible = {"raw", "nutzjson", "jackson", "gson", "jaxb", "jaxrs"})
    public static final String PROP_DECODER = PRE + "decoder";

    @PropDoc(value = "是否使用Hystrix", defaultValue = "false", possible = {"true", "false"})
    public static final String PROP_HYSTRIX_ENABLE = PRE + "hystrix.enable";

    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    @Inject
    protected AppContext appContext;

    @Inject
    protected PropertiesProxy conf;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object afterBorn(Object obj, String beanName) {
        try {
            Mirror mirror = Mirror.me(obj);
            for (Field field : obj.getClass().getDeclaredFields()) {
                FeignInject fc = field.getAnnotation(FeignInject.class);
                if (fc == null)
                    continue;
                Encoder encoder = getEncoder(fc, field);
                Decoder decoder = getDecoder(fc, field);
                Client client = getClient(fc, field);
                Logger.Level level = Level.valueOf(conf.get(PROP_LOGLEVEL, "BASIC").toUpperCase());
                String url = Strings.sBlank(conf.get(PROP_URL), "http://127.0.0.1:8080");
                Class apiType = field.getType();
                Logger logger = new Slf4jLogger(apiType);

                boolean useHystrix = "true".equals(Strings.sBlank(fc.useHystrix(), conf.get(PROP_HYSTRIX_ENABLE)));
                Feign.Builder builder = useHystrix ? HystrixFeign.builder() : Feign.builder();
                if (encoder != null)
                    builder.encoder(encoder);
                if (decoder != null)
                    builder.decoder(decoder);
                if (client != null)
                    builder.client(client);
                builder.logger(logger);
                builder.logLevel(level);
                Object t = null;
                if (useHystrix) {
                    t = ((HystrixFeign.Builder) builder).target(apiType, url, getIocBean(apiType, fc.fallback()));
                } else {
                    t = builder.target(apiType, url);
                }
                mirror.setValue(obj, field.getName(), t);
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

    protected Decoder getDecoder(FeignInject fc, Field field) {

        switch (Strings.sBlank(fc.decoder(), conf.get(PROP_DECODER, ""))) {
        case "":
        case "raw":
            break;
        case "nutzjson":
            return new NutzJsonDecoder();
        case "jackson":
            return new JacksonDecoder();
        case "gson":
            return new GsonDecoder();
        case "jaxb":
            JAXBContextFactory jaxbFactory = new JAXBContextFactory.Builder().withMarshallerJAXBEncoding("UTF-8")
                                                                             .withMarshallerSchemaLocation(getSchemaString(fc.schema()))
                                                                             .build();
            return new JAXBDecoder(jaxbFactory);
        default:
            break;
        }
        return null;
    }

    protected Encoder getEncoder(FeignInject fc, Field field) {
        switch (Strings.sBlank(fc.encoder(), conf.get(PROP_ENCODER, ""))) {
        case "":
        case "raw":
            break;
        case "nutzjson":
            return new NutzJsonEncoder();
        case "jackson":
            return new JacksonEncoder();
        case "gson":
            return new GsonEncoder();
        case "jaxb":
            JAXBContextFactory jaxbFactory = new JAXBContextFactory.Builder().withMarshallerJAXBEncoding("UTF-8")
                                                                             .withMarshallerSchemaLocation(getSchemaString(fc.schema()))
                                                                             .build();
            return new JAXBEncoder(jaxbFactory);
        default:
            break;
        }
        return null;
    }

    protected Client getClient(FeignInject fc, Field field) {
        switch (getClientString(fc)) {
        case "jdk":
            // nop
            break;
        case "okhttp":
            return new OkHttpClient();
        case "httpclient":
            return new ApacheHttpClient();
        case "ribbon":
            return RibbonClient.builder().lbClientFactory(new LBClientFactory() {
                public LBClient create(String clientName) {
                    return getLoadBalancer(clientName);
                }
            }).build();
        default:
            break;
        }
        return null;
    }

    public String getClientString(FeignInject fc) {
        return Strings.sBlank(fc.client(), conf.get(PROP_CLIENT, "jdk"));
    }

    public <T> T getIocBean(Class<T> apiType, String fallbackName) {
        if (!Strings.isBlank(fallbackName))
            return ioc.get(apiType, fallbackName);
        List<T> list = appContext.getBeans(apiType);
        if (list.size() > 0)
            return list.get(0);
        return null;
    }

    public String getSchemaString(String schema) {
        return Strings.sBlank(schema, conf.get(PROP_SCHEMA));
    }
    
    public LBClient getLoadBalancer(String name) {
        Provider<EurekaClient> provider = new Provider<EurekaClient>() {
            public EurekaClient get() {
                return ioc.get(EurekaClient.class, "eurekaClient");
            }
        };
        IRule rule = new RandomRule();
        DefaultClientConfigImpl clientConfig = DefaultClientConfigImpl.getClientConfigWithDefaultValues(name);
        ServerList<DiscoveryEnabledServer> list = new DiscoveryEnabledNIWSServerList(name, provider);
        ServerListFilter<DiscoveryEnabledServer> filter = new ZoneAffinityServerListFilter<DiscoveryEnabledServer>(clientConfig);
        ServerListUpdater updater = new EurekaNotificationServerListUpdater(provider);
        ZoneAwareLoadBalancer<DiscoveryEnabledServer> lb = LoadBalancerBuilder.<DiscoveryEnabledServer>newBuilder()
                .withDynamicServerList(list)
                .withRule(rule)
                .withServerListFilter(filter)
                .withServerListUpdater(updater)
                .withClientConfig(clientConfig)
                .buildDynamicServerListLoadBalancerWithUpdater();
        lb.enableAndInitLearnNewServersFeature();
        return LBClient.create(lb, clientConfig);
    }
}
