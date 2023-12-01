package org.nutz.boot.starter.feign;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;

import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.feign.annotation.FeignInject;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocEventListener;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.discovery.EurekaClient;
import com.netflix.loadbalancer.AvailabilityFilteringRule;
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
import feign.Request.Options;
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
import feign.RequestInterceptor;

@IocBean
public class FeignStarter implements IocEventListener {
    
    private static final Log log = Logs.get();

    protected static String PRE = "feign.";

    @PropDoc(value = "日志级别", defaultValue = "basic", possible = {"none", "basic", "headers", "full"})
    public static final String PROP_LOGLEVEL = PRE + "logLevel";

    @PropDoc(value = "Api Base URL", defaultValue = "http://127.0.0.1:8080")
    public static final String PROP_URL = PRE + "url";

    @PropDoc(value = "客户端实现类", defaultValue = "jdk", possible = {"jdk", "httpclient", "okhttp", "ribbon", "ioc:XXX"})
    public static final String PROP_CLIENT = PRE + "client";

    @PropDoc(value = "WebService的schema地址")
    public static final String PROP_SCHEMA = PRE + "schema";

    @PropDoc(value = "默认编码器", possible = {"raw", "nutzjson", "jackson", "gson", "jaxb", "jaxrs", "ioc:XXX"})
    public static final String PROP_ENCODER = PRE + "encoder";

    @PropDoc(value = "默认解码器", possible = {"raw", "nutzjson", "jackson", "gson", "jaxb", "jaxrs", "ioc:XXX"})
    public static final String PROP_DECODER = PRE + "decoder";

    @PropDoc(value = "是否使用Hystrix", defaultValue = "false", possible = {"true", "false"})
    public static final String PROP_HYSTRIX_ENABLE = PRE + "hystrix.enable";

    @PropDoc(value = "默认负载均衡的规则", defaultValue = "availability", possible = {"availability", "random"})
    public static final String PROP_LB_RULE = PRE + "loadbalancer.rule";

    @PropDoc(value = "JsonFormat", possible = {"full", "forLook", "compact", "nice", "tidy", "ioc:XXX",  "{...}"})
    public static final String PROP_JSON_FORMAT = PRE + "jsonFormat";

    @PropDoc(value = "连接超时", defaultValue="10000", type="int")
    public static final String PROP_CONNECT_TIMEOUT = PRE + "connectTimeout";

    @PropDoc(value = "读取超时", defaultValue="60000", type="int")
    public static final String PROP_READ_TIMEOUT = PRE + "readTimeout";

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
                String url = fc.apiBaseUrl();
                if (Strings.isBlank(url))
                    url = Strings.sBlank(conf.get(PROP_URL), "http://127.0.0.1:8080");
                Encoder encoder = getEncoder(fc, field);
                Decoder decoder = getDecoder(fc, field);
                Client client = getClient(fc, field, url);
                Logger.Level level = Level.valueOf(conf.get(PROP_LOGLEVEL, "BASIC").toUpperCase());
                Class apiType = field.getType();
                Logger logger = new Slf4jLogger(apiType);

                boolean useHystrix = "true".equals(Strings.sBlank(fc.useHystrix(), conf.get(PROP_HYSTRIX_ENABLE)));
                Feign.Builder builder = useHystrix ? HystrixFeign.builder() : Feign.builder();
                //注：仅支持查找第一个，按自然顺序排列
                RequestInterceptor interceptor = ioc.getByType(RequestInterceptor.class);
                if(interceptor!=null)
                	builder.requestInterceptor(interceptor);
                if (encoder != null)
                    builder.encoder(encoder);
                if (decoder != null)
                    builder.decoder(decoder);
                if (client != null)
                    builder.client(client);
                builder.logger(logger);
                builder.logLevel(level);
                int connectTimeout = fc.connectTimeout();
                if (connectTimeout == 0)
                    connectTimeout = conf.getInt(PROP_CONNECT_TIMEOUT, 10*1000);
                int readTimeout = fc.readTimeout();
                if (readTimeout == 0)
                    readTimeout = conf.getInt(PROP_READ_TIMEOUT, 10*1000);
                builder.options(new Options(connectTimeout, readTimeout));
                Object t = null;
                if (useHystrix) {
                    t = ((HystrixFeign.Builder) builder).target(apiType, url, getFallbackIocBean(apiType, fc.fallback()));
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
        String decoderStr = Strings.sBlank(fc.decoder(), conf.get(PROP_DECODER, ""));
        switch (decoderStr) {
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
            if (decoderStr.startsWith("ioc:"))
                return ioc.get(Decoder.class, decoderStr.substring(4));
            break;
        }
        return null;
    }

    protected Encoder getEncoder(FeignInject fc, Field field) {
        String encoderStr = Strings.sBlank(fc.encoder(), conf.get(PROP_ENCODER, ""));
        switch (encoderStr) {
        case "":
        case "raw":
            break;
        case "nutzjson":
            JsonFormat jf = JsonFormat.full();
            String jfStr = Strings.sBlank(fc.jsonFormat(), conf.get(PROP_JSON_FORMAT, ""));
            if (!Strings.isBlank(jfStr)) {
                if (jfStr.startsWith("{")) {
                    jf = Json.fromJson(JsonFormat.class, jfStr);
                }
                else if (jfStr.startsWith("ioc:")) {
                    jf = ioc.get(JsonFormat.class, jfStr.substring(4));
                }
                else {
                   try {
                        jf = (JsonFormat) JsonFormat.class.getMethod(jfStr).invoke(null);
                    }
                     catch (Exception e) {
                         log.infof("invaild JsonFormat=[%s] at %s", jfStr, field);
                    }
                }
            }
            return new NutzJsonEncoder(jf);
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
            if (encoderStr.startsWith("ioc"))
                return ioc.get(Encoder.class, encoderStr.substring(4));
            break;
        }
        return null;
    }

    protected Client getClient(FeignInject fc, Field field, String url) {
        String clientStr = getClientString(fc);
        switch (clientStr) {
        case "jdk":
            // nop
            break;
        case "okhttp":
            return new OkHttpClient();
        case "httpclient":
            return new ApacheHttpClient();
        case "ribbon":
            LBClient lb = (LBClient)getLoadBalancer(URI.create(url).getHost(), fc);
            return RibbonClient.builder().lbClientFactory(new LBClientFactory() {
                public LBClient create(String clientName) {
                    return lb;
                }
            }).build();
        default:
            if (clientStr.startsWith("ioc:"))
                return ioc.get(Client.class, clientStr.substring(4));
            break;
        }
        return null;
    }

    public String getClientString(FeignInject fc) {
        return Strings.sBlank(fc.client(), conf.get(PROP_CLIENT, "jdk"));
    }

    public <T> T getFallbackIocBean(Class<T> apiType, String fallbackName) {
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
    
    public String getLbRuleString(String lbRule) {
        return Strings.sBlank(lbRule, conf.get(PROP_LB_RULE, "availability"));
    }
    
    public Object getLoadBalancer(String name, FeignInject fc) {
        EurekaClient eurekaClient = ioc.get(EurekaClient.class, "eurekaClient");
        DefaultClientConfigImpl clientConfig = DefaultClientConfigImpl.getClientConfigWithDefaultValues(name);
        ServerList<DiscoveryEnabledServer> list = new DiscoveryEnabledNIWSServerList(name, ()->eurekaClient);
        ServerListFilter<DiscoveryEnabledServer> filter = new ZoneAffinityServerListFilter<DiscoveryEnabledServer>(clientConfig);
        ServerListUpdater updater = new EurekaNotificationServerListUpdater(()->eurekaClient);

        IRule rule = null;
        switch (getLbRuleString(fc.lbRule())) {
        case "random":
            rule = new RandomRule();
            break;
        case "availability":
        default:
            AvailabilityFilteringRule _rule = new AvailabilityFilteringRule();
            _rule.initWithNiwsConfig(clientConfig);
            rule = _rule;
            break;
        }
        ZoneAwareLoadBalancer<DiscoveryEnabledServer> lb = LoadBalancerBuilder.<DiscoveryEnabledServer>newBuilder()
                .withDynamicServerList(list)
                .withRule(rule)
                .withServerListFilter(filter)
                .withServerListUpdater(updater)
                .withClientConfig(clientConfig)
                .buildDynamicServerListLoadBalancerWithUpdater();
        return LBClient.create(lb, clientConfig);
    }
}
