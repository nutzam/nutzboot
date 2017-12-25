package org.nutz.boot.starter.feign;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.nutz.boot.starter.feign.annotation.FeignClient;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;

/**
 *
 */
@IocBean(create = "init")
public class FeignStarter {

    private static final Log log = Logs.get();

    @Inject
    protected PropertiesProxy conf;


    @Inject
    protected FeignRegister feignRegister;

    public void init() {
        String prefix = "feign.";
        for (String key : conf.getKeys()) {
            if (key.length() < prefix.length() + 1 || !key.startsWith(prefix))
                continue;
            String name = key.substring(prefix.length());
            if ("pkgs".equals(name)) {
                log.debug("found feign packages = " + conf.get(key));
                for (String pkg : Strings.splitIgnoreBlank(conf.get(key), ",")) {
                    addPackage(pkg);
                }
                continue;
            }
        }
    }

    public <T> void addPackage(String pkg) {
        for (Class<?> klass : Scans.me().scanPackage(pkg)) {
            FeignClient feignClient = klass.getAnnotation(FeignClient.class);
            String key = klass.getSimpleName();

            if (feignClient != null) {
                T o = (T) Feign.builder()
                        .encoder(new JacksonEncoder())
                        .decoder(new JacksonDecoder())
                        .target(klass, "http://localhost:8080");
                //iocContext.save("app",klass.getSimpleName(),objectProxy.setObj(o));
                feignRegister.add(key,o);
                log.debug("feignRegister cache "+key);
            }
        }

    }
}
