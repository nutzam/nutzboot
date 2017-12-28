package org.nutz.boot.starter.swagger;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.boot.AppContext;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.resource.Scans;

import io.swagger.models.Info;
import io.swagger.models.Swagger;
import io.swagger.servlet.Reader;
import io.swagger.util.Json;

@IocBean
public class SwaggerServletStarter extends HttpServlet implements WebServletFace {
    
	private static final long serialVersionUID = 988318972932805253L;

	@Inject
    protected PropertiesProxy conf;

    @Inject
    protected AppContext appContext;
    
    protected Swagger swagger;

    public String getName() {
        return "swagger";
    }

    public String getPathSpec() {
        return "/swagger/swagger.json";
    }

    public Servlet getServlet() {
        return this;
    }

    public Map<String, String> getInitParameters() {
        return new HashMap<>();
    }
    

    public void init(ServletConfig config) throws ServletException {
        PropertiesProxy conf = appContext.getConfigureLoader().get();
        swagger = conf.makeDeep(Swagger.class, "swagger.conf.");
        Info info = conf.makeDeep(Info.class, "swagger.info.");
        swagger.setInfo(info);
        HashSet<Class<?>> classes = new HashSet<>();
        String pkgName = conf.get("swagger.resource.package", appContext.getPackage());
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
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(Json.mapper().writeValueAsString(swagger));
        } else {
            response.setStatus(404);
        }
    }

}
