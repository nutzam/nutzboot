package org.nutz.boot.starter.ureport;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.sql.DataSource;

import org.nutz.boot.tools.SpringWebContextProxy;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;

import com.bstek.ureport.Utils;
import com.bstek.ureport.definition.datasource.BuildinDatasource;
import com.bstek.ureport.provider.image.ImageProvider;

@IocBean
public class UreportSpringEnvStarter extends SpringWebContextProxy {

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
        if (appContext.getIoc().has("dataSource")) {
            // 添加一个内置的BuildinDatasource
            DataSource ds = appContext.getIoc().get(DataSource.class);
            buildinDatasources.add(new BuildinDatasource() {
                public String name() {
                    return "nutztboot.buildin";
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
        buildinDatasources.addAll(Utils.getBuildinDatasources());
        Mirror.me(Utils.class).setValue(null, "buildinDatasources", buildinDatasources);
        // 把ImageProvider也注册一下
        List<ImageProvider> images = appContext.getBeans(ImageProvider.class);
        images.addAll(Utils.getImageProviders());
        Mirror.me(Utils.class).setValue(null, "imageProviders", images);
    }
}
