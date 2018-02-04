package org.nutz.boot.starter.freemarker;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 */
public class JspSupportServlet extends HttpServlet {

    private static final long serialVersionUID = 8302309812391541933L;
    public static JspSupportServlet jspSupportServlet;
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        jspSupportServlet = this;
    }
}
