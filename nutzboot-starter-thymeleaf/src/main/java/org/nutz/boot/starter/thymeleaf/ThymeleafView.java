package org.nutz.boot.starter.thymeleaf;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.util.Context;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.view.AbstractPathView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

public class ThymeleafView extends AbstractPathView {

    private static final Log log = Logs.get();

    private TemplateEngine templateEngine = new TemplateEngine();

    private String contentType;

    private String encoding;

    public ThymeleafView(ClassLoader classLoader, NutMap prop, String path) {
        super(path);
        templateEngine.setTemplateResolver(initializeTemplateResolver(classLoader, prop));

        encoding = prop.getString("encoding", "UTF-8");
        contentType = prop.getString("contentType", "text/html") + "; charset=" + encoding;
    }

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object value) throws Exception {
        String path = evalPath(request, value);
        response.setContentType(contentType);
        response.setCharacterEncoding(encoding);
        try {
            Context ctx = createContext(request, value);
            WebContext context = new WebContext(request,
                    response,
                    Mvcs.getServletContext(),
                    Locale.getDefault(),
                    ctx.getInnerMap());
            templateEngine.process(path, context, response.getWriter());
        } catch (Exception e) {
            log.error("模板引擎错误", e);
            throw e;
        }
    }

    private ITemplateResolver initializeTemplateResolver(ClassLoader classLoader, NutMap prop) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver(classLoader);

        templateResolver.setTemplateMode(prop.getString("mode", "HTML"));
        templateResolver.setPrefix(prop.getString("prefix", "template/"));
        templateResolver.setSuffix(prop.getString("suffix", ".html"));
        templateResolver.setCharacterEncoding(prop.getString("encoding", "UTF-8"));
        templateResolver.setCacheable(prop.getBoolean("cache", true));
        templateResolver.setCacheTTLMs(prop.getLong("cacheTTLMs", 3600000L));

        return templateResolver;
    }
}
