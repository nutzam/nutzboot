package org.nutz.boot.config.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Disks;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 配置信息优先级, 从低到高: <p/>
 * <p/>custom目录下的配置文件
 * <p/>application.properties
 * <p/>application-${profile}.properties
 * <p/>命令行参数
 */
public class PropertiesConfigureLoader extends AbstractConfigureLoader {
    
    private static final Log log = Logs.get();

    public void init() throws Exception {
    	// 首先, 确定一些从什么路径加载配置文件,默认值application.properties
        String path = envHolder.get("nutz.boot.configure.properties_path", "application.properties");
        // 另外,加载custom目录下的配置文件,与nutzcn一致
        conf.setPaths("custom/");
        // 如果当前文件夹存在application.properties,读取之
        boolean flag = true;
        try {
            File tmp = new File(getPath(path));
            if (tmp.exists() && tmp.canRead()) {
                try (FileInputStream ins = new FileInputStream(tmp)) {
                    log.debugf("load %s", tmp.getAbsolutePath());
                    conf.load(Streams.utf8r(ins), false);
                    flag = false;
                }
            }
        }
        catch (Throwable e) {
        }
        if (flag) {
            // 加载application.properties
            readPropertiesPath(path);
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
        	String _path = path.substring(0, path.lastIndexOf('.')) + "-" + profile + ".properties";
        	readPropertiesPath(_path);
        }
        // 如果conf内含有nutz.boot.configure.properties.dir配置，则读取该目录下的所有配置文件
        // 配置示例： nutz.boot.configure.properties.dir=config, 那么读取的就是jar包当前目录下config子目录下的所有properties文件
        if(conf.has("nutz.boot.configure.properties.dir")) {
            String configDir = conf.get("nutz.boot.configure.properties.dir");
            String configPath = getPath(configDir);
            Disks.visitFile(configPath, ".+properties$", true, (file)->{
                if (file.canRead())
                    try {
                        try (FileInputStream ins = new FileInputStream(file)) {
                            conf.load(Streams.utf8r(ins), false);
                        }
                    }
                    catch (IOException e) {
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
    
    protected void readPropertiesPath(String path) throws IOException {
        try (InputStream ins = resourceLoader.get(path)) {
            if (ins != null) {
                if (log.isDebugEnabled())
                    log.debug("Loading Properties  - " + path);
                conf.load(Streams.utf8r(ins), false);
            }
            else {
                if (log.isInfoEnabled())
                    log.info("Properties NotFound - " + path);
            }
        }
    }
}
