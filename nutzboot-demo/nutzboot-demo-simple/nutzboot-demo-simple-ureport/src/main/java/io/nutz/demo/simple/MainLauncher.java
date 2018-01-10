package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.bstek.ureport.Utils;
import com.bstek.ureport.export.ExportManager;

@IocBean(create = "init")
public class MainLauncher {

    private static final Log log = Logs.get();

    @Inject
    protected ExportManager exportManager;

    public void init() {
        log.info("BuildinDatasource count=" + Utils.getBuildinDatasources().size());
    }

    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }

}
