package org.nutz.boot.config.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Streams;

/**
 * 配置信息优先级, 从低到高: <p/>
 * <p/>custom目录下的配置文件
 * <p/>application.properties
 * <p/>application-${profile}.properties
 * <p/>命令行参数
 */
public class PropertiesConfigureLoader extends AbstractConfigureLoader {

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
            File tmp = new File(path);
            if (tmp.exists() && tmp.canRead()) {
                try (FileInputStream ins = new FileInputStream(tmp)) {
                    conf.load(Streams.utf8r(ins), false);
                }
            }
        }
        catch (Throwable e) {
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
