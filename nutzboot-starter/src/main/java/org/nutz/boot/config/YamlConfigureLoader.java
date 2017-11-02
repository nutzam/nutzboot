package org.nutz.boot.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.nutz.lang.util.LifeCycle;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class YamlConfigureLoader extends NutMap implements ConfigureLoader, LifeCycle {
    
    private static final Log log = Logs.get();
    
    private static final long serialVersionUID = 1L;

    public void init() throws Exception {
        // 首先,尝试读取application.yml
        String fromEnv = System.getProperty("nutz.configure.yaml_path", "application.yml");
        InputStream ins = null;
        File f = new File(fromEnv);
        if (f.exists()) {
            // 看来文件是存在的,读取之
            log.debug("Loading Configure from " + f.getAbsolutePath());
            readYaml(new FileInputStream(f));
            return;
        }
        else {
            // ClassPath中可以读到
            ins = getClass().getClassLoader().getResourceAsStream(fromEnv);
            if (ins != null) {
                readYaml(ins);
                return;
            }
        }
        // 再试试application.properties
        fromEnv = System.getProperty("nutz.configure.properties_path", "application.properties");
        f = new File(fromEnv);
        if (f.exists()) {
            readProperties(new FileInputStream(f));
        }
        else {
            ins = getClass().getClassLoader().getResourceAsStream(fromEnv);
            if (ins != null) {
                readProperties(ins);
                return;
            }
        }
        throw new RuntimeException("eithe application.yml or application.properties found");
    }
    
    protected void readYaml(InputStream ins ) {
        
    }
    
    protected void readProperties(InputStream ins) {
        
    }

    public void fetch() throws Exception {
    }

    public void depose() throws Exception {
    }
    
    
}
