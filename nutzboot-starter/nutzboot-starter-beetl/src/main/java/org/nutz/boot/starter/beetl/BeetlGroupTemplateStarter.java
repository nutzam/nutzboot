package org.nutz.boot.starter.beetl;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.core.resource.FileResourceLoader;
import org.beetl.ext.nutz.LogErrorHandler;
import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean
public class BeetlGroupTemplateStarter {

    protected static final String PRE = "beetl.";

    @PropDoc(value = "占位符的定界符的起始符号", defaultValue = "${")
    public static final String PROP_DELIMITER_PLACEHOLDER_START = PRE + Configuration.DELIMITER_PLACEHOLDER_START;

    @PropDoc(value = "占位符的定界符的结束符号", defaultValue = "}")
    public static final String PROP_DELIMITER_PLACEHOLDER_END = PRE + Configuration.DELIMITER_PLACEHOLDER_END;

    @PropDoc(value = "语句的定界符的起始符号", defaultValue = "<%")
    public static final String PROP_DELIMITER_STATEMENT_START = PRE + Configuration.DELIMITER_STATEMENT_START;

    @PropDoc(value = "语句的定界符的结束符号", defaultValue = "%>")
    public static final String PROP_DELIMITER_STATEMENT_END = PRE + Configuration.DELIMITER_STATEMENT_END;

    @PropDoc(value = "第二组占位符的定界符的起始符号")
    public static final String PROP_DELIMITER_PLACEHOLDER_START2 = PRE
                                                                   + Configuration.DELIMITER_PLACEHOLDER_START2;

    @PropDoc(value = "第二组占位符的定界符的结束符号")
    public static final String PROP_DELIMITER_PLACEHOLDER_END2 = PRE
                                                                 + Configuration.DELIMITER_PLACEHOLDER_END2;

    @PropDoc(value = "第二组语句的定界符的起始符号")
    public static final String PROP_DELIMITER_STATEMENT_START2 = PRE
                                                                 + Configuration.DELIMITER_STATEMENT_START2;

    @PropDoc(value = "第二组语句的定界符的结束符号")
    public static final String PROP_DELIMITER_STATEMENT_END2 = PRE
                                                               + Configuration.DELIMITER_STATEMENT_END2;

    @PropDoc(value = "是否允许原生调用", defaultValue = "false")
    public static final String PROP_NATIVE_CALL = PRE + Configuration.NATIVE_CALL;

    @PropDoc(value = "是否忽略客户端IO错误", defaultValue = "false")
    public static final String PROP_IGNORE_CLIENT_IO_ERROR = PRE + Configuration.IGNORE_CLIENT_IO_ERROR;

    @PropDoc(value = "直接输出字节流", defaultValue = "true")
    public static final String PROP_DIRECT_BYTE_OUTPUT = PRE + Configuration.DIRECT_BYTE_OUTPUT;

    @PropDoc(value = "模板目录的路径", defaultValue = "template/")
    public static final String PROP_TEMPLATE_ROOT = PRE + "RESOURCE.root";

    @PropDoc(value = "模板目录的绝对路径,若不存在,回落到'模板目录的路径'")
    public static final String PROP_TEMPLATE_ROOT_LOCAL = PRE + "RESOURCE.rootLocal";

    @PropDoc(value = "自动检测模板更新", defaultValue = "true")
    public static final String PROP_TEMPLATE_AUTO_CHECK = PRE + "RESOURCE.autoCheck";

    @PropDoc(value = "模板字符集", defaultValue = "UTF-8")
    public static final String PROP_TEMPLATE_CHARSET = PRE + Configuration.TEMPLATE_CHARSET;

    @PropDoc(value = "错误处理器", defaultValue = "org.beetl.ext.nutz.LogErrorHandler")
    public static final String PROP_ERROR_HANDLER = PRE + Configuration.ERROR_HANDLER;

    @PropDoc(value = "MVC严格模式", defaultValue = "false")
    public static final String PROP_MVC_STRICT = PRE + Configuration.MVC_STRICT;

    @PropDoc(value = "扩展全局变量的实现类")
    public static final String PROP_WEBAPP_EXT = PRE + Configuration.WEBAPP_EXT;

    @PropDoc(value = "是否支持Html标签", defaultValue = "false")
    public static final String PROP_HTML_TAG_SUPPORT = PRE + Configuration.HTML_TAG_SUPPORT;

    @PropDoc(value = "html标签前缀", defaultValue = "#")
    public static final String PROP_HTML_TAG_FLAG = PRE + Configuration.HTML_TAG_FLAG;

    @PropDoc(value = "需要导入哪些package")
    public static final String PROP_IMPORT_PACKAGE = PRE + Configuration.IMPORT_PACKAGE;

    @PropDoc(value = "渲染引擎", defaultValue = "org.beetl.core.engine.FastRuntimeEngine")
    public static final String PROP_ENGINE = PRE + Configuration.ENGINE;

    @PropDoc(value = "本地支持的安全管理器", defaultValue = "org.beetl.core.DefaultNativeSecurityManager")
    public static final String PROP_NATIVE_SECUARTY_MANAGER = PRE + Configuration.NATIVE_SECUARTY_MANAGER;

    @PropDoc(value = "模板加载器", defaultValue = "org.beetl.core.resource.ClasspathResourceLoader")
    public static final String PROP_RESOURCE_LOADER = PRE + Configuration.RESOURCE_LOADER;

    @PropDoc(value = "html标签绑定属性", defaultValue = "var,export")
    public static final String PROP_HTML_TAG_BINDING_ATTRIBUTE = PRE + Configuration.HTML_TAG_BINDING_ATTRIBUTE;

    private static final Log log = Logs.get();

    @Inject
    protected PropertiesProxy conf;

    @Inject
    protected AppContext appContext;

    @IocBean(depose = "close")
    public GroupTemplate getGroupTemplate() throws IOException {
        Properties prop = new Properties();
        for (String key : conf.keySet()) {
            if (key.startsWith("beetl.")) {
                prop.put(key.substring("beetl.".length()), conf.get(key));
            }
        }
        if (!prop.containsKey(Configuration.RESOURCE_LOADER)) {
            prop.put(Configuration.RESOURCE_LOADER, ClasspathResourceLoader.class.getName());
        }
        if (!prop.containsKey("RESOURCE.autoCheck")) {
            prop.put("RESOURCE.autoCheck", "true");
        }
        if (!prop.containsKey("RESOURCE.root")) {
            prop.put("RESOURCE.root", prop.getProperty("root", "template/"));
        }
        if (!prop.containsKey(Configuration.DIRECT_BYTE_OUTPUT)) {
            // 默认启用DIRECT_BYTE_OUTPUT,除非用户自定义, 一般不会.
            log.debug("no custom DIRECT_BYTE_OUTPUT found , set to true");
            // 当DIRECT_BYTE_OUTPUT为真时, beetl渲染会通过getOutputStream获取输出流
            // 而BeetlView会使用LazyResponseWrapper代理getOutputStream方法
            // 从而实现在模板输出之前,避免真正调用getOutputStream
            // 这样@Fail视图就能正常工作了
            prop.put(Configuration.DIRECT_BYTE_OUTPUT, "true");
        }
        if (!prop.containsKey(Configuration.ERROR_HANDLER)) {
            // 没有自定义ERROR_HANDLER,用定制的
            prop.put(Configuration.ERROR_HANDLER, LogErrorHandler.class.getName());
        }
        Configuration cfg = new Configuration(prop);
        String local = conf.get(PROP_TEMPLATE_ROOT_LOCAL);
        GroupTemplate gt = new GroupTemplate(cfg);
        if (!Strings.isBlank(local)) {
            try {
                if (new File(local).exists()) {
                    local = new File(local).getAbsolutePath();
                    FileResourceLoader resourceLoader = new FileResourceLoader(local);
                    resourceLoader.setAutoCheck(true);
                    gt.setResourceLoader(resourceLoader);
                    log.debugf("Template Local path=%s is ok, use it", local);
                }
            }
            catch (Throwable e) {
                log.infof("Template Local path=%s is not vaild, fallback to beetl.RESOURCE.root", local, e);
            }
        }
        return gt;
    }
}
