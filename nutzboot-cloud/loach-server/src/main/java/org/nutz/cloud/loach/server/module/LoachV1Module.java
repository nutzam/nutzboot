package org.nutz.cloud.loach.server.module;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.lang.util.Regex;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.DELETE;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;

@IocBean
@At("/loach/v1")
@Ok("json304")
public class LoachV1Module {

    protected JsonFormat jsonFormat = JsonFormat.compact();
    
    @Inject
    protected PropertiesProxy conf;

    /**
     * 供客户端心跳入口
     */
    @At({"/ping", "/ping/?/?"})
    @Aop("redis")
    @GET
    public String ping(String serviceName, String id) {
        if (id != null && id.length() < 30) {
            long re = jedis().expire("loach:service:" + serviceName + ":" + id, getPingTimeout() / 1000);
            if (re == 0) {
                return "{ok:false}";
            }
        }
        return "{ok:true}";
    }

    /**
     * 注册一个服务,必须带service.vip和service.name,其他属性可任选.
     */
    @AdaptBy(type = JsonAdaptor.class)
    @POST
    @At
    @Aop("redis")
    public NutMap reg(NutMap params) {
        NutMap map = new NutMap();
        // 检查基本的信息
        if (Strings.isBlank(params.getString("vip"))) {
            map.put("err", "miss vip");
            return map;
        }
        String serviceName = (String) params.remove("name");
        if (Strings.isBlank(serviceName)) {
            map.put("err", "miss name");
            return map;
        }
        if (!Regex.match("^[a-zA-Z0-9_-]{3,16}$", serviceName)) {
            map.put("err", "name is invaild");
            return map;
        }
        String regJson = Json.toJson(params, jsonFormat);
        if (regJson.length() > getRegMaxSize()) {
            map.put("err", "reg info is too big");
            return map;
        }
        String id = params.getString("id");
        if (id == null)
            id = R.UU32();
        jedis().setex("loach:service:" + serviceName + ":" + id, getPingTimeout() / 1000, regJson);
        map.setv("ok", true).setv("id", id);
        return map;
    }

    @At
    @DELETE
    @Ok("void")
    public void unreg(String serviceName, String id) {
        if (isAllowUnreg())
            jedis().del("loach:service:" + serviceName + ":" + id);
    }

    @At("/list/?")
    @Aop("redis")
    public NutMap list(String serviceName) {
        List<String> keys = new ArrayList<>(jedis().keys("loach:service:" + serviceName + ":*"));
        Collections.sort(keys);
        
        NutMap re = new NutMap();
        re.put("ok", true);
        List<NutMap> services = new LinkedList<>();
        for (String key : keys) {
            String cnt = jedis().get(key);
            if (cnt == null)
                continue;
            NutMap serviceInfo = Json.fromJson(NutMap.class, cnt);
            services.add(serviceInfo);
        }
        re.put("data", new NutMap(serviceName, services));
        return re;
    }

    @At("/list")
    public NutMap listAll() {
        NutMap re = new NutMap();
        re.put("ok", true);
        re.put("data", getAllServices());
        return re;
    }

    @Ok("raw")
    @At("/list/forlook")
    public String listAllForLook(boolean verbose) {
        StringBuilder sb = new StringBuilder();
        Map<String, List<NutMap>> services = getAllServices();
        String NL = "\r\n";
        sb.append("Service Count : ").append(services.size()).append(NL);
        if (services.size() > 0) {
            sb.append("Services :").append(NL);
            for (String serviceName : services.keySet()) {
                sb.append("  - ").append(serviceName).append(" :").append(NL);
                for (NutMap service : services.get(serviceName)) {
                    sb.append("    - ").append(service.get("id"));
                    if (verbose) {
                        sb.append(":").append(NL);
                        for (Map.Entry<String, Object> en2 : service.entrySet()) {
                            sb.append("      - ").append(en2.getKey()).append(" : ").append(en2.getValue()).append(NL);
                        }
                    } else {
                        sb.append(" : ").append(service.get("vip")).append(":").append(service.getInt("port")).append(NL);
                    }
                }
            }
        }
        return sb.toString();
    }

    @Aop("redis")
    protected Map<String, List<NutMap>> getAllServices() {
        List<String> keys = new ArrayList<>(jedis().keys("loach:service:*"));
        Collections.sort(keys);
        Map<String, List<NutMap>> services = new HashMap<>();
        for (String key : keys) {
            String[] tmp = key.split("\\:");
            if (tmp.length != 4)
                continue;
            String cnt = jedis().get(key);
            if (cnt == null)
                continue;
            NutMap serviceInfo = Json.fromJson(NutMap.class, cnt);
            List<NutMap> infos = services.get(tmp[2]);
            if (infos == null) {
                infos = new LinkedList<>();
                services.put(tmp[2], infos);
            }
            serviceInfo.put("id", tmp[3]);
            infos.add(serviceInfo);
        }
        return services;
    }
    
    public int getPingTimeout() {
        return conf.getInt("loach.server.ping.timeout", 15000);
    }
    
    public int getRegMaxSize() {
        return conf.getInt("loach.server.reg.maxSize", 8192);
    }
    
    public boolean isAllowUnreg() {
        return conf.getBoolean("loach.server.unreg.enable", false);
    }
}
