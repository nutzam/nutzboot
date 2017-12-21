package org.nutz.boot.starter.velocity;


import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.nutz.lang.Lang;
import org.nutz.mvc.view.AbstractPathView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 */
public class VelocityView extends AbstractPathView {


    private VelocityEngine engine;
    private String templateClasspath;
    private String charsetEncoding;

    public VelocityView(String dest, VelocityEngine engine, String templateClasspath, String charsetEncoding) {
        super(dest);
        this.engine = engine;
        this.templateClasspath = templateClasspath;
        this.charsetEncoding = charsetEncoding;
    }


    @Override
    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable {
        resp.setCharacterEncoding(charsetEncoding);
        if (resp.getContentType() == null) {
            resp.setContentType("text/html; charset=" + charsetEncoding);
        }
        try {
            String templateUrl = templateClasspath + evalPath(req, obj);
            Template template = engine.getTemplate(templateUrl, charsetEncoding);
            VelocityWebContext webContext = new VelocityWebContext(req, resp);
            VelocityContext context = new VelocityContext(webContext);
            PrintWriter writer = resp.getWriter();
            template.merge(context, writer);
            //writer.flush();
        } catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }
    
}
