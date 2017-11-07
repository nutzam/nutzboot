package io.nutz.demo.simple;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.nutz.boot.NbApp;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

@IocBean
public class MainLauncher {
    
    @Inject
    protected PropertiesProxy conf;
    
    @At("/")
    @Ok("->:/index.html")
    public void index() {}
    
    @AdaptBy(type=JsonAdaptor.class)
    @At("/maker/make")
    @Ok("json:full")
    public NutMap make(@Param("..")NutMap params) {
        NutMap re = new NutMap();
        String key = R.UU32();
        String tmpDir = conf.get("nutz.maker.tmpdir", "/tmp/maker");
        File tmpRoot = Files.createDirIfNoExists(tmpDir + "/" + key);
        build(tmpRoot, params);
        re.put("key", key);
        re.put("ok", true);
        return re;
    }
    
    @At("/maker/download/?")
    @Ok("raw:zip")
    public void download(String key) {
        
    }
    
    protected void build(File tmpRoot, NutMap params) {
        // 首先,生成pom.xml
        List<NutMap> starter = new ArrayList<>();
        for (NutMap nutMap : starter) {
            
        }
    }

    public static void main(String[] args) throws Exception {
        new NbApp(MainLauncher.class).run();
    }

}
