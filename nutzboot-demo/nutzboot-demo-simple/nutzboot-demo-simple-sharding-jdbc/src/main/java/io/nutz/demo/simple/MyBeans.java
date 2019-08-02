package io.nutz.demo.simple;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.nutz.boot.AppContext;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;

import io.shardingjdbc.core.api.ShardingDataSourceFactory;

@IocBean
public class MyBeans{
    @Inject
    protected PropertiesProxy conf;

    @Inject
    protected AppContext appContext;

    @IocBean(name="dao2")
    public Dao getDao2() throws Exception {
        String path = "shardingjdbc2.yaml";
        InputStream ins = appContext.getResourceLoader().get(path);
        if (ins == null) {
            File f = new File(path);
            if (f.exists() && f.canRead()) {
                ins = new FileInputStream(f);
            } else {
                throw new RuntimeException("no such shardingjdbc configure file=" + path);
            }
        }
        return new NutDao(ShardingDataSourceFactory.createDataSource(Streams.readBytesAndClose(ins)));
    }
}
