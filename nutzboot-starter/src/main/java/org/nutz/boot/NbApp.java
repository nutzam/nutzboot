package org.nutz.boot;

import org.nutz.boot.config.ConfigureLoader;
import org.nutz.boot.config.impl.YamlConfigureLoader;
import org.nutz.boot.ioc.NbIocLoader;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.lang.Strings;
import org.nutz.log.LogAdapter;
import org.nutz.log.Logs;

public class NbApp {

    public static void main(String[] args) throws Exception {
        // 就是NB
        // 初始化上下文
        AppContext ctx = AppContext.getDefault();
        // 检查ClassLoader的情况
        if (ctx.getClassLoader() == null)
            ctx.setClassLoader(NbApp.class.getClassLoader());
        
        // 看看日志应该用哪个
        String logAdapter = System.getProperty("nutz.boot.base.LogAdapter");
        if (Strings.isBlank(logAdapter)) {
            Logs.get();
        } else {
            Logs.setAdapter((LogAdapter) ctx.getClassLoader().loadClass(logAdapter).newInstance());
        }
        // 配置信息要准备好
        if (ctx.getConfigure() == null) {
            String cnfLoader = System.getProperty("nutz.boot.base.ConfigureLoader");
            ConfigureLoader configure;
            if (Strings.isBlank(cnfLoader)) {
                configure = new YamlConfigureLoader();
            } else {
                configure = (ConfigureLoader) ctx.getClassLoader().loadClass(cnfLoader).newInstance(); 
            }
            ctx.setConfigure(configure);
        }
        
        // 创建Ioc容器
        if (ctx.getIoc() == null) {
            ctx.setIoc(new NutIoc(new NbIocLoader()));
        }
        
        // 加载各种Starter
        
        // 排序各种starter
        
        // 依次启动
        
        // 等待关闭
        
        // 收尾
    }

}
