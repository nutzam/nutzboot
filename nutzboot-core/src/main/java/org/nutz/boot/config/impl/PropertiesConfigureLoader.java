package org.nutz.boot.config.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Encoding;
import org.nutz.lang.Streams;
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

    // 获取应用程序绝对路径
    protected String getBasePath() {
        String basePath = "";
        basePath = appContext.getMainClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        int lastIndex = basePath.lastIndexOf(File.separator);
        basePath = basePath.substring(0, lastIndex);
        try {
            basePath = URLDecoder.decode(basePath, Encoding.UTF8);
        } catch (UnsupportedEncodingException e) {
        }
        return basePath;
    }

    // 根据目录和文件名拼接绝对路径
    protected String getPath(String... name) {
        String path = getBasePath();
        for(int i=0; i< name.length; i++) {
            path = path + File.separator + name[i];
        }
        return path;
    }

    public void init() throws Exception {
    	// 首先, 确定一些从什么路径加载配置文件,默认值application.properties
        String path = envHolder.get("nutz.boot.configure.properties_path", "application.properties");
        // 另外,加载custom目录下的配置文件,与nutzcn一致
        conf.setPaths("custom/");
        // 加载application.properties
        try (InputStream ins = resourceLoader.get(path)) {
            if (ins != null) {
                conf.load(Streams.utf8r(ins), false);
            }
        }
        // 如果当前文件夹存在application.properties,读取之
        try {
            File tmp = new File(getPath(path));
            if (tmp.exists() && tmp.canRead()) {
                try (FileInputStream ins = new FileInputStream(tmp)) {
                    conf.load(Streams.utf8r(ins), false);
                }
            }
        }
        catch (Throwable e) {
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
        	path = path.substring(0, path.lastIndexOf('.')) + "-" + profile + ".properties";
        	try (InputStream ins = resourceLoader.get(path)) {
                if (ins != null) {
                    conf.load(Streams.utf8r(ins), false);
                }
            }
        }
        // 把命令行参数放进去
        if (tmp.size() > 0) {
        	conf.putAll(tmp.toMap());
        }
    }
}
