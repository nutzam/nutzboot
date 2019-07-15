package org.nutz.boot.starter.loach.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.nutz.boot.AppContext;
import org.nutz.boot.NbApp;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.lang.hardware.NetworkType;
import org.nutz.lang.hardware.Networks;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.runner.NutRunner;

/**
 * '泥鳅'客户端
 * 
 * @author wendal
 *
 */
@IocBean(create = "init", depose = "depose")
public class LoachClient extends NutRunner {

    public LoachClient() {
        super("loach.client");
    }

    private static final Log log = Logs.get();

    @Inject
    protected PropertiesProxy conf;

    @Inject
    protected AppContext appContext;
    
    @Inject("refer:$ioc")
    protected Ioc ioc;

    @Inject
    protected NbApp nbApp;

    protected String id;

    protected int startUpDelay;

    protected ExecutorService es;
    
    protected NutRunner updater;
    
    protected List<UpdateListener> listeners = new LinkedList<>();
    
    public static interface UpdateListener {
        void onUpdate(Map<String, List<NutMap>> services);
    }

    public void depose() {
        getLock().stop();
        if (updater != null)
            updater.getLock().stop();
        if (es != null)
            es.shutdown();
    }

    public void init() {
        startUpDelay = conf.getInt("loach.client.startUpDelay", -1);
        es = Executors.newCachedThreadPool();
        id = R.UU32();
        url = conf.get("loach.client.url", "http://127.0.0.1:8610/loach/v1");
        for (String name : ioc.getNamesByType(UpdateListener.class)) {
            addListener(ioc.get(UpdateListener.class, name));
        }

        setDebug(conf.getBoolean("loach.client.debug", false));
        if (conf.getBoolean("loach.client.enable", true)) {
            if (Strings.isBlank(conf.get("nutz.application.name"))) {
                throw new RuntimeException("need nutz.application.name");
            }
        }
        if (conf.getBoolean("loach.client.enable", true)) {
            es.submit(this);
        }
        if (conf.getBoolean("loach.updater.enable", true)) {
            updateServiceList();
            updater = new NutRunner("loach.updater." + url) {
                public long exec() throws Exception {
                    LoachClient.this.updateServiceList();
                    return conf.getInt("loach.client.updater.interval", 3000);
                }
            };
            updater.setDebug(isDebug());
            es.submit(updater);
        }
    }

    public Map<String, List<NutMap>> getServiceList() {
        return Collections.unmodifiableMap(serviceList);
    }

    public List<NutMap> getService(String name) {
        if (serviceList == null)
            return null;
        return serviceList.get(name);
    }

    protected String getUrls() {
        return conf.get("loach.client.url", "http://127.0.0.1:8610/loach/v1");
    }

    protected String getServiceName() {
        return conf.get("nutz.application.name", "demo");
    }

    protected long getPingInterval() {
        return conf.getLong("loach.client.ping.interval", 3000);
    }

    protected int pingRetryCount;

    public String url;

    protected String lastPingETag = "ABC";

    protected String lastListETag = "ABC";

    protected boolean regOk;

    protected boolean _reg(String regData) {
        try {
            String regURL = url + "/reg";
            if (isDebug()) {
                log.debug("Reg URL :" + regURL);
                log.debug("Reg Data:" + regData);
            }
            Request req = Request.create(regURL, METHOD.POST);
            req.setData(regData);
            req.getHeader().clear();
            req.getHeader().asJsonContentType();
            Response resp = Sender.create(req).setTimeout(3000).send();
            if (resp.isOK()) {
                NutMap re = Json.fromJson(NutMap.class, resp.getReader());
                if (re != null && re.getBoolean("ok", false)) {
                    log.infof("Reg Done id=%s url=%s", id, url);
                    regOk = true;
                    return true;
                }
                else if (re == null) {
                    log.info("Reg Err, revc NULL");
                    return false;
                }
                else {
                    log.info("Reg Err " + re);
                    return false;
                }
            }
        }
        catch (Throwable e) {
            log.debugf("bad url? %s %s", url, e.getMessage());
        }
        return false;
    }

    protected boolean _ping() {
        try {
            String pingURL = url + "/ping/" + getServiceName() + "/" + id;
            if (isDebug())
                log.debug("Ping URL=" + pingURL);
            Request req = Request.create(pingURL, METHOD.GET);
            req.getHeader().clear();
            req.getHeader().set("If-None-Match", lastPingETag);
            Response resp = Sender.create(req, conf.getInt("loach.client.ping.timeout", 1000)).setConnTimeout(1000).send();
            String cnt = resp.getContent();
            if (isDebug())
                log.debug("Ping result : " + cnt);
            if (resp.isOK()) {
                lastPingETag = Strings.sBlank(resp.getHeader().get("ETag"), "ABC");
                NutMap re = Json.fromJson(NutMap.class, cnt);
                if (re != null && re.getBoolean("ok", false))
                    return true;
            } else if (resp.getStatus() == 304) {
                return true;
            }
        }
        catch (Throwable e) {
            log.debugf("bad url? %s %s", url, e.getMessage());
        }
        return false;
    }

    /**
     * 主逻辑
     */
    public long exec() throws Exception {
        // 启动延时
        if (startUpDelay > 0) {
            log.debug("start up delay " + startUpDelay + "ms");
            int delay = startUpDelay;
            startUpDelay = 0;
            return delay;
        } else if (startUpDelay == -1) {
            if (!nbApp.isStarted())
                return 100;
        }
        // 心跳
        if (regOk) {
            // 尝试心跳,成功就是返回
            if (_ping()) {
                if (pingRetryCount > 0 && isDebug())
                    log.debug("loach client ping OK");
                pingRetryCount = 0;
                return getPingInterval();
            } else {
                if (pingRetryCount < 5) {
                    pingRetryCount++;
                    log.info("loach client ping FAIL count=" + pingRetryCount);
                    return getInterval();
                } else {
                    regOk = false;
                    log.info("loach client ping FAIL too many time, redo reg!");
                }
            }
        }
        NutMap regInfo = new NutMap();
        regInfo.put("id", id);
        regInfo.put("name", getServiceName());
        regInfo.put("vip", conf.get("server.vip", "127.0.0.1"));
        regInfo.put("port", appContext.getServerPort(null));
        regInfo.put("eth.mac", Networks.mac(NetworkType.LAN));
        regInfo.put("eth.ipv4", Networks.ipv4(NetworkType.LAN));
        regInfo.putAll(EXT_REG_DATA);
        String regData = Json.toJson(regInfo, JsonFormat.compact());
        _reg(regData);
        return getInterval();
    }

    public Map<String, List<NutMap>> serviceList = new HashMap<>();
    protected long lastChecked;

    @SuppressWarnings("unchecked")
    public void updateServiceList() {
        try {
            String listURL = url + "/list";
            Request req = Request.create(listURL, METHOD.GET);
            req.getHeader().clear();
            req.getHeader().set("If-None-Match", lastListETag);
            Response resp = Sender.create(req).setConnTimeout(1000).setTimeout(3000).send();
            if (resp.isOK()) {
                serviceList = (Map<String, List<NutMap>>) Json.fromJson(NutMap.class, resp.getReader()).get("data");
                for (UpdateListener listener : listeners) {
                    listener.onUpdate(serviceList);
                }
                lastChecked = System.currentTimeMillis();
                lastListETag = Strings.sBlank(resp.getHeader().get("ETag", "ABC"));
            } else if (resp.getStatus() == 304) {
                // ok
                lastChecked = System.currentTimeMillis();
            }
        }
        catch (Throwable e) {
            log.debugf("bad url? %s %s", url, e.getMessage());
        }
    }
    
    public void addListener(UpdateListener listener) {
        if (this.listeners.contains(listener))
            return;
        listeners.add(listener);
    }

    public static NutMap EXT_REG_DATA = new NutMap();
}
