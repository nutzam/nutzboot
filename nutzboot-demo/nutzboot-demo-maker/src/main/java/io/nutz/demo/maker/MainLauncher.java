package io.nutz.demo.maker;

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
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@IocBean(create="init")
public class MainLauncher {
    
    @Inject
    protected PropertiesProxy conf;
    
    protected TemplateEngine engine;
    
    protected File tmpDir;
    
    @At("/")
    @Ok("->:/index.html")
    public void index() {}
    
    @AdaptBy(type=JsonAdaptor.class)
    @At("/maker/make")
    @Ok("json:full")
    public NutMap make(@Param("..")NutMap params) throws IOException {
        NutMap re = new NutMap();
        String key = R.UU32();
        File tmpRoot = Files.createDirIfNoExists(tmpDir + "/" + key);
        build(tmpRoot, params);
        re.put("key", key);
        re.put("ok", true);
        return re;
    }
    
    @At("/maker/download/?")
    @Ok("raw:zip")
    public File download(String key) {
        return new File(tmpDir + "/" + key + ".zip");
    }
    
    protected void build(File tmpRoot, NutMap params) throws IOException {
        // 首先,生成pom.xml
        String pomStr = _render("_pom.xml", params);
        Files.write(new File(tmpRoot, "pom.xml"), pomStr);
        
        // 接下来,生成src/main里面的东西
        // 生成application.properties
        String applicationPropertiesStr = _render("src/main/resources/application.properties", params);
        _write(new File(tmpRoot, "src/main/resources/application.properties"), applicationPropertiesStr);
        
        // 生成log4j.properties
        String log4jPropertiesStr = _render("src/main/resources/log4j.properties", params);
        _write(new File(tmpRoot, "src/main/resources/log4j.properties"), log4jPropertiesStr);
        
        // 生成MainLauncher
        String packagePath = params.getString("packageName").replace('.', '/');
        String mainLauncherStr = _render("src/main/java/_package/MainLauncher.java", params);
        _write(new File(tmpRoot, "src/main/java/"+packagePath + "/MainLauncher.java"), mainLauncherStr);
        
        // maven wrapper from https://github.com/takari/maven-wrapper
        String key = tmpRoot.getName();
        _copy(key, "mvnw");
        _copy(key, "mvnw.cmd");
        _copy(key, ".mvn/wrapper/maven-wrapper.properties");
        _copy(key, ".mvn/wrapper/maven-wrapper.jar");
        
        // 拷贝个简介
        _copy(key, "Readme");
        
        // 打包,手工
        zipIt(tmpRoot);
    }
    
    protected void _write(File target, String value) {
        Files.write(Files.createFileIfNoExists(target), value);
    }
    
    protected void _copy(String key, String from) {
        Files.write(Files.createFileIfNoExists(new File(tmpDir, key + "/" + from)), getClass().getClassLoader().getResourceAsStream("static/thymeleaf/" + from));
    }
    
    protected String _render(String name, NutMap params) {
        WebContext ctx = new WebContext(Mvcs.getReq(), Mvcs.getResp(), Mvcs.getServletContext());
        ctx.setVariable("params", params);
        return engine.process("thymeleaf/"+name, ctx);
    }
    
    protected void zipIt(File tmpRoot) throws IOException {
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(new File(tmpRoot.getParentFile(), tmpRoot.getName() + ".zip")));
        Disks.visitFile(tmpRoot, new FileVisitor() {
            public void visit(File file) {
                try {
                    if (file.isDirectory())
                        return;
                    String name = file.getAbsolutePath().substring(tmpRoot.getAbsolutePath().length() + 1);
                    System.out.println(">>" + name);
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
    
    public void init() {
        engine = new TemplateEngine();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(Mvcs.getServletContext());
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        engine.setTemplateResolver(templateResolver);
        tmpDir = new File(conf.get("nutz.maker.tmpdir", "/tmp/maker")).getAbsoluteFile();
    }

    public static void main(String[] args) throws Exception {
        new NbApp().run();
    }

}
