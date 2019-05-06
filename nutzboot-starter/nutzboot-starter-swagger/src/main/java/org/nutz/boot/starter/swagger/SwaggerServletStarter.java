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
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.Ioc;
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
	
	protected static final String PRE = "swagger.";
	
	@PropDoc(value = "是否启用swagger", defaultValue = "true", type = "boolean")
    public static final String PROP_ENABLE = PRE + "enable";

	@Inject
    protected PropertiesProxy conf;

    @Inject
    protected AppContext appContext;

    public String getName() {
        return "swagger";
    }

    public String getPathSpec() {
        return "/swagger/swagger.json";
    }

    public Servlet getServlet() {
        if (!conf.getBoolean(PROP_ENABLE, true))
            return null;
        return this;
    }

    public Map<String, String> getInitParameters() {
        return new HashMap<>();
    }
    
    @IocBean(name="swagger")
    public Swagger createSwagger() {
        return conf.makeDeep(Swagger.class, "swagger.conf.");
    }
    
    @IocBean(name="swaggerInfo")
    public Info createSwaggerInfo() {
        return conf.makeDeep(Info.class, "swagger.info.");
    }
    

    public void init(ServletConfig config) throws ServletException {
        Ioc ioc = appContext.getIoc();
        Swagger swagger = ioc.get(Swagger.class);
        swagger.setInfo(ioc.get(Info.class, "swaggerInfo"));
        HashSet<Class<?>> classes = new HashSet<>();
        String pkgName = conf.get("swagger.resource.package", appContext.getPackage());
        for (Class<?> klass : Scans.me().scanPackage(pkgName)) {
            classes.add(klass);
        }
        Reader.read(swagger, classes);
        config.getServletContext().setAttribute("swagger", swagger);
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final String pathInfo = request.getRequestURI();
        if (pathInfo.endsWith("/swagger.json")) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(Json.mapper().writeValueAsString(request.getServletContext().getAttribute("swagger")));
        } else {
            response.setStatus(404);
        }
    }

}
