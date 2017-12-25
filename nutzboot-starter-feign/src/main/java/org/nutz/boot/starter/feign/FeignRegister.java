package org.nutz.boot.starter.feign;

import org.nutz.ioc.loader.annotation.IocBean;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@IocBean(name = "feignRegister")
public  class FeignRegister {

    private static Map<String,Object> register = new HashMap<String,Object>();

    public <T> void add(String key,T t){
        register.put(key,t);
    }

    public <T> T get(String key){
       return (T) register.get(key);
    }

}
