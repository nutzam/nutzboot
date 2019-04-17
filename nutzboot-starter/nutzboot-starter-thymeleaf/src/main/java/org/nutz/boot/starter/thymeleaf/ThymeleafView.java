package org.nutz.boot.starter.thymeleaf;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.view.AbstractPathView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

public class ThymeleafView extends AbstractPathView {

    private static final Log log = Logs.get();

    protected TemplateEngine templateEngine;

    protected String contentType;
    protected String encoding;
    
    public ThymeleafView(TemplateEngine templateEngine, String path, String contentType, String encoding) {
        super(path);
        this.templateEngine = templateEngine;
        this.contentType = contentType;
        this.encoding = encoding;
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
}
