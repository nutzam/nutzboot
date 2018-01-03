package org.nutz.boot.tools;

import static org.nutz.lang.Strings.dup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.lang.Strings;

/**
 * 默认的@ProcDoc读取器
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class PropDocReader {

    /**
     * 全局上下文
     */
    protected AppContext appContext;
    /**
     * 文档列表
     */
    protected Map<String, PropDocBean> docs = new HashMap<>();
    
    public void load(List<Class<?>> starterClasses) throws Exception {
        for (Class<?> klass : starterClasses) {
            addClass(klass);
        }
    }

    /**
     * 逐个添加需要加载的类
     * 
     * @param klass
     *            需要加载的类
     * @throws Exception
     *             不太可能会抛出异常的异常
     */
    public void addClass(Class<?> klass) throws Exception {
        // 只读取public的属性
        for (Field field : klass.getFields()) {
            // 只读取PROP_开头的属性
            if (!field.getName().startsWith("PROP_"))
                continue;
            // 不带@ProcDoc? 跳过
            PropDoc prop = field.getAnnotation(PropDoc.class);
            if (prop == null)
                continue;
            // 如果不设置key,那就取字段的值,通常也是这样
            String key = Strings.isBlank(prop.key()) ? (String) field.get(null) : prop.key();

            // 如果对于的key不存在,那就新建一个PropDocBean,否则就取老的
            PropDocBean doc = docs.get(key);
            if (doc == null) {
                doc = new PropDocBean();
                doc.key = key;
                doc.defaultValue = prop.defaultValue();
                doc.value = prop.value();
                doc.group = Strings.isBlank(prop.group()) ? doc.key.substring(0, doc.key.indexOf('.')) : prop.group();
                doc.need = prop.need();
                doc.possible = prop.possible();
                doc.users = new ArrayList<>();
                docs.put(key, doc);
            }
            // 把使用这个key的类统统登记一下
            doc.users.add(klass.getName());
        }
    }

    // 输出markdown格式
    public String toMarkdown() {
        String fm = "|%-4s|%-40s|%-10s|%-20s|%-10s|%-20s|%40s|\r\n";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(fm, "id", "key", "required", "Possible Values", "Default", "Description", "starters"));
        sb.append(String.format(fm, dup("-", 4), dup("-", 40), dup("-", 10), dup("-", 20), dup("-", 10), dup("-", 20), dup("-", 40)));
        int index = 0;
        ArrayList<String> keys = new ArrayList<>(docs.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            PropDocBean doc = docs.get(key);
            sb.append(String.format(fm, index, key, doc.need ? "yes" : "no", Strings.join(",", doc.possible), doc.defaultValue, doc.value, Strings.join(",", doc.users)));
            index++;
        }
        return sb.toString().trim();
    }

    /**
     * 获取原始文档数据,通常用于生成Json文本
     * 
     * @return 原始文档数据
     */
    public Map<String, PropDocBean> getDocs() {
        return docs;
    }

}
