package org.nutz.start.swagger;

import java.io.IOException;
import java.util.HashSet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.boot.AppContext;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;
import org.nutz.resource.Scans;

import io.swagger.models.Swagger;
import io.swagger.servlet.Reader;
import io.swagger.util.Json;
import io.swagger.util.Yaml;

@SuppressWarnings("serial")
public class SwaggerServlet extends HttpServlet {

    protected AppContext appContext;
    
    protected Swagger swagger;
    
    public SwaggerServlet setAppContext(AppContext appContext) {
        this.appContext = appContext;
        return this;
    }
    
    public void init(ServletConfig config) throws ServletException {
        swagger = new Swagger();
        swagger.setBasePath(config.getServletContext().getContextPath());
        HashSet<Class<?>> classes = new HashSet<>();
        String pkgName = appContext.getConfigureLoader().get().get("swagger.packageName");
        if (Strings.isBlank(pkgName)) {
            pkgName = appContext.getMainClass().getPackage().getName();
        }
        for (Class<?> klass : Scans.me().scanPackage(pkgName)) {
            classes.add(klass);
        }
        Reader.read(swagger, classes);
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final String pathInfo = request.getRequestURI();
        if (pathInfo.endsWith("/swagger.json")) {
            response.setContentType("application/json");
            response.getWriter().println(Json.mapper().writeValueAsString(swagger));
        } else if (pathInfo.endsWith("/swagger.yaml")) {
            response.setContentType("application/yaml");
            response.getWriter().println(Yaml.mapper().writeValueAsString(swagger));
        } else if (pathInfo.endsWith("/")) {
            
        } else {
            response.setStatus(404);
        }
    }
}
