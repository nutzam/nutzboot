package io.nutz.demo.simple;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.nutz.boot.NbApp;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.random.R;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.FileVisitor;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@IocBean(create="init")
public class MainLauncher {
    
    @Inject
    protected PropertiesProxy conf;
    
    protected TemplateEngine engine;
    
    @At("/")
    @Ok("->:/index.html")
    public void index() {}
    
    @AdaptBy(type=JsonAdaptor.class)
    @At("/maker/make")
    @Ok("json:full")
    public NutMap make(@Param("..")NutMap params) throws IOException {
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
    public File download(String key) {
        String tmpDir = conf.get("nutz.maker.tmpdir", "/tmp/maker");
        return new File(tmpDir + "/" + key + ".zip");
    }
    
    protected void build(File tmpRoot, NutMap params) throws IOException {
        // 首先,生成pom.xml
        String pomStr = _render("pom_xml", params);
        System.out.println(pomStr);
        Files.write(new File(tmpRoot, "pom.xml"), pomStr);
        
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(new File(tmpRoot.getParentFile(), tmpRoot.getName() + ".zip")));
        Disks.visitFile(tmpRoot, new FileVisitor() {
            public void visit(File file) {
                try {
                    if (file.isDirectory())
                        return;
                    String name = file.getAbsolutePath().substring(tmpRoot.getAbsolutePath().length() + 1);
                    zip.putNextEntry(new ZipEntry(name));
                    try (FileInputStream ins = new FileInputStream(file)) {
                        Streams.write(zip, ins);
                    }
                    zip.closeEntry();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, null);
        zip.finish();
        zip.flush();
        zip.close();
    }
    
    protected String _render(String name, NutMap params) {
        WebContext ctx = new WebContext(Mvcs.getReq(), Mvcs.getResp(), Mvcs.getServletContext());
        ctx.setVariable("params", params);
        return engine.process("thymeleaf/"+name+".html", ctx);
    }
    
    public void init() {
        engine = new TemplateEngine();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(Mvcs.getServletContext());
        engine.setTemplateResolver(templateResolver);
    }

    public static void main(String[] args) throws Exception {
        new NbApp(MainLauncher.class).run();
    }

}
