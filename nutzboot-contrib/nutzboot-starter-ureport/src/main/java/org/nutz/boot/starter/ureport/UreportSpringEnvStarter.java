package org.nutz.boot.starter.ureport;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.sql.DataSource;

import org.nutz.boot.tools.SpringWebContextProxy;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import com.bstek.ureport.Utils;
import com.bstek.ureport.definition.datasource.BuildinDatasource;
import com.bstek.ureport.provider.image.ImageProvider;

@IocBean
public class UreportSpringEnvStarter extends SpringWebContextProxy {

    @Inject
    protected PropertiesProxy conf;
    
    public UreportSpringEnvStarter() {
        configLocation = "classpath:ureport-spring-context.xml";
        selfName = "ureport";
    }

    @Override
    protected List<String> getSpringBeanNames() {
        List<String> names = super.getSpringBeanNames();
        names.remove(selfName + ".props");
        return names;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        super.contextInitialized(sce);
        // 把BuildinDatasource统统注册一下
        List<BuildinDatasource> buildinDatasources = appContext.getBeans(BuildinDatasource.class);
        if (!Strings.isBlank(conf.get("ureport.nutzboot.dsName"))) {
            // 添加一个内置的BuildinDatasource
            DataSource ds = appContext.getIoc().get(DataSource.class);
            buildinDatasources.add(new BuildinDatasource() {
                public String name() {
                    return conf.get("ureport.nutzboot.dsName");
                }

                public Connection getConnection() {
                    try {
                        return ds.getConnection();
                    }
                    catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        Utils.getBuildinDatasources().addAll(buildinDatasources);
        // 把ImageProvider也注册一下
        Utils.getImageProviders().addAll(appContext.getBeans(ImageProvider.class));
    }
}
