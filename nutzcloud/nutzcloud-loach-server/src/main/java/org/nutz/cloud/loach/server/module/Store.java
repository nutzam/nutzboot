package org.nutz.cloud.loach.server.module;

import java.util.List;

public interface Store {
    /**
     * 是否存在
     * @param key
     * @return
     */
    Boolean has(String key);

    /**
     * 存储
     * @param key
     * @param val
     */
    void put(String key, String val);

    /**
     * 删除
     * @param key
     */
    void del(String key);

    /**
     * 所有Key
     * @return
     */
    List<String> keys(String pattern);

    /**
     * 获取数据
     * @param key
     * @return
     */
    String get(String key);
}
