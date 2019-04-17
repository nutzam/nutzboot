package org.nutz.boot.starter.thymeleaf;

import java.io.File;

import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

@IocBean(name = "$views_thymeleaf", create = "init")
public class ThymeleafViewMakerStarter implements ViewMaker {

    private static final Log log = Logs.get();

    public static String PRE = "thymeleaf.";

    @PropDoc(value = "渲染模式", defaultValue = "html")
    public static final String PROP_MODE = PRE + "mode";

    @PropDoc(value = "路径前缀", defaultValue = "template/")
    public static final String PROP_PREFIX = PRE + "prefix";

    @PropDoc(value = "模板文件后缀", defaultValue = ".html")
    public static final String PROP_SUFFIX = PRE + "suffix";

    @PropDoc(value = "模板文件编码", defaultValue = "UTF-8")
    public static final String PROP_ENCODING = PRE + "encoding";

    @PropDoc(value = "启用模板缓存", defaultValue = "true")
    public static final String PROP_CACHE_ENABLE = PRE + "cache.enable";

    @PropDoc(value = "模板缓存生存时长", defaultValue = "3000")
    public static final String PROP_CACHE_TTL_MS = PRE + "cache.ttl";

    @PropDoc(value = "模板目录的绝对路径,若不存在,回落到'模板目录的路径'")
    public static final String PROP_TEMPLATE_ROOT_LOCAL = PRE + "resolver.rootLocal";

    @PropDoc(value = "加载dialects,需要完整类名,逗号分隔")
    public static final String PROP_DIALECTS = PRE + "dialects";

    @PropDoc(value = "带前缀加载dialect,需要完整类名,逗号分隔")
    public static final String PROP_DIALECTS_XXX = PRE + "dialects.xx";

    @PropDoc(value = "响应的默认类型", defaultValue="text/html")
    public static final String PROP_CONTENT_TYPE = PRE + "contentType";

    @Inject
    protected PropertiesProxy conf;

    @Inject
    protected AppContext appContext;

    protected NutMap prop = NutMap.NEW();

    protected TemplateEngine templateEngine = new TemplateEngine();

    protected String contentType;

    protected String encoding;
    
    protected AbstractConfigurableTemplateResolver setupTemplateResolver(AbstractConfigurableTemplateResolver templateResolver) {

        templateResolver.setTemplateMode(conf.get(PROP_MODE, "HTML"));
        templateResolver.setPrefix(conf.get(PROP_PREFIX, "template/"));
        templateResolver.setSuffix(conf.get(PROP_SUFFIX, ".html"));
        templateResolver.setCharacterEncoding(encoding);
        templateResolver.setCacheable(conf.getBoolean(PROP_CACHE_ENABLE, true));
        templateResolver.setCacheTTLMs(conf.getLong(PROP_CACHE_TTL_MS, 3000L));
        
        return templateResolver;
    }

    protected ClassLoaderTemplateResolver createClassLoaderTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver(appContext.getClassLoader());
        setupTemplateResolver(templateResolver);
        return templateResolver;
    }

    protected FileTemplateResolver createFileTemplateResolver(String root) {
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        setupTemplateResolver(templateResolver);
        templateResolver.setPrefix(root);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    public void init() {
        log.debug("thymeleaf init ....");
        
        contentType = conf.get(PROP_CONTENT_TYPE, "text/html");
        encoding = conf.get(PROP_ENCODING, "UTF-8");
        
        if (conf.has("thymeleaf.dialects")) {
            addDialect(null, conf.get("thymeleaf.dialects"));
        }
        for (String key : conf.keySet()) {
            if (key.startsWith("thymeleaf.dialects.")) {
                String prefix = key.substring("thymeleaf.dialects.".length());
                if ("default".equals(prefix))
                    prefix = null;
                String dialects = conf.get(key);
                addDialect(prefix, dialects);
            }
        }
        if (conf.has(PROP_TEMPLATE_ROOT_LOCAL)) {
            String path = conf.get(PROP_TEMPLATE_ROOT_LOCAL);
            File f = new File(path);
            if (f.exists()) {
                log.debugf("add local template path = %s", f.getAbsolutePath());
                FileTemplateResolver resolver = createFileTemplateResolver(f.getAbsolutePath());
                resolver.setCacheable(false);
                templateEngine.addTemplateResolver(resolver);
            }
        }
        templateEngine.addTemplateResolver(createClassLoaderTemplateResolver());
        log.debug("thymeleaf init complete");
    }

    public void addDialect(String prefix, String klassNames) {
        for (String dialect : Strings.splitIgnoreBlank(klassNames)) {
            log.debugf("loading thymeleaf dialect " + dialect);
            Class<?> klass = Lang.loadClassQuite(dialect);
            if (klass == null) {
                log.info("no such thymeleaf dialect class = " + dialect);
                continue;
            }
            Object obj = Mirror.me(klass).born();
            if (obj instanceof IDialect) {
                templateEngine.addDialect(prefix, (IDialect) obj);
            }
        }
    }

    @Override
    public View make(Ioc ioc, String type, String value) {
        if ("th".equalsIgnoreCase(type)) {
            return new ThymeleafView(templateEngine, value, contentType, encoding);
        }
        return null;
    }

    @IocBean(name = "thymeleafTemplateEngine")
    public TemplateEngine getTemplateEngine() {
        return templateEngine;
    }
}
