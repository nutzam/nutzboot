package org.nutz.boot.config.impl;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Disks;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wendal(wendal1985@gmail.com)
 * @author wizzer(wizer.cn@gmail.com)
 */
public class YamlConfigureLoader extends AbstractConfigureLoader {

    private static final Log log = Logs.get();

    public void init() throws Exception {
        String path = envHolder.get("nutz.boot.configure.yaml_path", "application.yaml");
        // 另外,加载custom目录下的配置文件,与nutzcn一致
        conf.setPaths("custom/");
        // 如果当前文件夹存在application.yaml,读取之
        boolean flag = true;
        try {
            File tmp = new File(getPath(path));
            if (tmp.exists() && tmp.canRead()) {
                try (FileInputStream ins = new FileInputStream(tmp)) {
                    log.debugf("load %s", tmp.getAbsolutePath());
                    yamlToProperties(new Yaml().loadAs(Streams.utf8r(ins), Map.class));
                    flag = false;
                }
            }
        } catch (Throwable e) {
        }
        if (flag) {
            // 加载application.yaml
            readYamlPath(path);
        }
        // 也许命令行里面指定了profile,需要提前load进来
        PropertiesProxy tmp = new PropertiesProxy();
        if (args != null) {
            parseCommandLineArgs(tmp, args);
            if (tmp.has("nutz.profiles.active")) {
                conf.put("nutz.profiles.active", tmp.remove("nutz.profiles.active"));
            }
        }
        if (allowCommandLineProperties) {
            conf.putAll(System.getProperties());
        }
        // 加载指定profile,如果有的话
        if (conf.has("nutz.profiles.active")) {
            String profile = conf.get("nutz.profiles.active");
            String _path = path.substring(0, path.lastIndexOf('.')) + "-" + profile + ".yaml";
            readYamlPath(_path);
        }
        // 如果conf内含有nutz.boot.configure.yaml.dir配置，则读取该目录下的所有配置文件
        // 配置示例： nutz.boot.configure.yaml.dir=config, 那么读取的就是jar包当前目录下config子目录下的所有yaml文件
        if (conf.has("nutz.boot.configure.yaml.dir")) {
            String configDir = conf.get("nutz.boot.configure.yaml.dir");
            String configPath = getPath(configDir);
            Disks.visitFile(configPath, ".+yaml", true, (file) -> {
                if (file.canRead())
                    try {
                        try (FileInputStream ins = new FileInputStream(file)) {
                            yamlToProperties(new Yaml().loadAs(Streams.utf8r(ins), Map.class));
                        }
                    } catch (IOException e) {
                        log.info("fail to load " + file.getAbsolutePath());
                    }
            });
        }
        // 把命令行参数放进去
        if (tmp.size() > 0) {
            conf.putAll(tmp.toMap());
        }
        if (Strings.isBlank(conf.get("app.build.version"))) {
            InputStream ins = resourceLoader.get("build.version");
            if (ins != null) {
                conf.load(new InputStreamReader(ins), false);
            }
        }
    }

    // 根据目录和文件名拼接绝对路径
    protected String getPath(String... names) {
        String tmp = Strings.join(File.separator, names);
        if (tmp.endsWith("/"))
            tmp = tmp.substring(0, tmp.length() - 1);
        File f = new File(tmp);
        if (f.exists()) {
            String path = Disks.getCanonicalPath(tmp);
            String path2 = Disks.getCanonicalPath(f.getAbsolutePath());
            if (path.equals(path2))
                return tmp;
        }
        return appContext.getBasePath() + File.separator + tmp;
    }

    protected void readYamlPath(String path) throws IOException {
        try (InputStream ins = resourceLoader.get(path)) {
            if (ins != null) {
                if (log.isDebugEnabled())
                    log.debug("Loading yaml  - " + path);
                yamlToProperties(new Yaml().loadAs(Streams.utf8r(ins), Map.class));
            } else {
                if (log.isInfoEnabled())
                    log.info("Yaml NotFound - " + path);
            }
        }
    }

    private void yamlToProperties(Map map) {
        Map<String, Object> result = new HashMap<>();
        buildFlattenedMap(result, map, "");
        conf.putAll(result);
    }

    private void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, String path) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = Strings.sNull(entry.getKey());
            if (Strings.isNotBlank(path)) {
                if (key.startsWith("[")) {
                    key = path + key;
                } else {
                    key = path + "." + key;
                }
            }
            Object value = entry.getValue();
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) value;
                buildFlattenedMap(result, map, key);
            } else if (value instanceof Collection) {
                @SuppressWarnings("unchecked")
                Collection<Object> collection = (Collection<Object>) value;
                StringBuilder val = new StringBuilder();
                for (Object object : collection) {
                    String str = Strings.sNull(object);
                    if (str.startsWith("{") && str.endsWith("}")) {
                        val.append(Json.toJson(object, JsonFormat.compact())).append("\n");
                    } else {
                        val.append(str).append("\n");
                    }
                }
                result.put(key, val.toString());
            } else {
                result.put(key, Strings.sNull(value));
            }
        }
    }
}
