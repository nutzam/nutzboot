package org.nutz.cloud.loach.server.module.impl;

import org.nutz.cloud.loach.server.module.Store;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@IocBean(name = "mapStore")
public class MapStoreImpl implements Store {
    Map<String, String> datas = new ConcurrentHashMap();

    @Override
    public Boolean has(String key) {
        return datas.containsKey(key);
    }

    @Override
    public void put(String key, String val) {
        datas.put(key, val);
    }

    @Override
    public void del(String key) {
        datas.remove(key);
    }

    @Override
    public List<String> keys(String pattern) {
        return new ArrayList<>(datas.keySet());
    }

    @Override
    public String get(String key) {
        return datas.get(key);
    }
}
