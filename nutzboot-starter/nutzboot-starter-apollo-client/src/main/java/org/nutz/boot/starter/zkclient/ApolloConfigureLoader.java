package org.nutz.boot.starter.zkclient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.boot.config.impl.AbstractConfigureLoader;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Lang;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;

public class ApolloConfigureLoader extends AbstractConfigureLoader {

    public void init() throws Exception {
        Config config = ConfigService.getAppConfig();
        conf = new PropertiesProxy() {
            public String get(String key) {
                return maps.getOrDefault(key, config.getProperty(key, null));
            }

            public List<String> keys() {
                ArrayList<String> _keys = new ArrayList<>();
                _keys.addAll(maps.keySet());
                _keys.addAll(config.getPropertyNames());
                return _keys;
            }

            public Collection<String> values() {
                ArrayList<String> values = new ArrayList<>();
                keys().forEach((key) -> values.add(get(key)));
                return values;
            }

            public List<String> getKeys() {
                return keys();
            }

            public boolean containsKey(Object key) {
                return maps.containsKey(key) || config.getPropertyNames().contains(key);
            }

            public boolean containsValue(Object value) {
                throw Lang.noImplement();
            }

            public Map<String, String> toMap() {
                Map<String, String> map = new HashMap<>();
                keys().forEach((key) -> map.put(key, get(key)));
                return map;
            }

            public Set<Entry<String, String>> entrySet() {
                return toMap().entrySet();
            }

            public boolean isEmpty() {
                return keys().size() == 0;
            }
        };
    }

}
