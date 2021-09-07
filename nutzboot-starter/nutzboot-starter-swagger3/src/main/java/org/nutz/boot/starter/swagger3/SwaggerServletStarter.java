package org.nutz.boot.starter.swagger3;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.jaxrs2.integration.OpenApiServlet;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.apache.commons.lang3.StringUtils;
import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/2/10
 */
@IocBean
public class SwaggerServletStarter extends OpenApiServlet implements WebServletFace {
    private static final Log log = Logs.get();

    @Inject
    protected PropertiesProxy conf;

    @Inject
    protected AppContext appContext;

    protected static final String PRE = "swagger.";

    @PropDoc(value = "是否启用swagger", defaultValue = "true", type = "boolean")
    public static final String PROP_ENABLE = PRE + "enable";

    @PropDoc(value = "扫描包路径", type = "string")
    public static final String PROP_RESOURCE_PACKAGES = PRE + "scanner.package";

    public Map<String, String> getInitParameters() {
        return new HashMap<>();
    }

    @IocBean(name = "swaggerInfo")
    public Info createSwaggerInfo() {
        Info info = conf.makeDeep(Info.class, PRE + "info.");
        info.setContact(conf.makeDeep(Contact.class, PRE + "info.contact."));
        info.setLicense(conf.makeDeep(License.class, PRE + "info.license."));
        return info;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        Ioc ioc = appContext.getIoc();
        String pkg = conf.get(PROP_RESOURCE_PACKAGES, appContext.getPackage());
        try {
            OpenAPI oas = new OpenAPI();
            oas.info(ioc.get(Info.class, "swaggerInfo"));
            Set<String> pkgs = new HashSet<>();
            pkgs.add(pkg);
            SwaggerConfiguration oasConfig = new SwaggerConfiguration()
                    .openAPI(oas)
                    .prettyPrint(true)
                    .resourcePackages(pkgs);
            HashSet<Class<?>> classes = new HashSet<>();
            for (Class<?> klass : Scans.me().scanPackage(pkg)) {
                classes.add(klass);
            }
            OpenAPI api = new NutzReader(oasConfig).read(classes);
            config.getServletContext().setAttribute("swagger", api);
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String pathInfo = req.getRequestURI();
        if (pathInfo.endsWith("/swagger.json") || pathInfo.endsWith("/swagger.yaml")) {
            OpenAPI oas = (OpenAPI) req.getServletContext().getAttribute("swagger");
            String type = "json";
            String acceptHeader = req.getHeader("Accept");
            if (!StringUtils.isBlank(acceptHeader) && acceptHeader.toLowerCase().contains("application/yaml")) {
                type = "yaml";
            } else if (req.getRequestURL().toString().toLowerCase().endsWith("yaml")) {
                type = "yaml";
            }

            boolean pretty = true;
            resp.setStatus(200);
            PrintWriter pw;
            if (type.equalsIgnoreCase("yaml")) {
                resp.setContentType("application/yaml");
                pw = resp.getWriter();
                pw.write(pretty ? Yaml.pretty(oas) : Yaml.mapper().writeValueAsString(oas));
                pw.close();
            } else {
                resp.setContentType("application/json");
                pw = resp.getWriter();
                pw.write(pretty ? Json.pretty(oas) : Json.mapper().writeValueAsString(oas));
                pw.close();
            }

        } else {
            resp.setStatus(404);
        }
    }

    @Override
    public String getName() {
        return "swagger";
    }

    @Override
    public String getPathSpec() {
        return "";
    }

    /**
     * 设置访问路径
     *
     * @return
     */
    @Override
    public String[] getPathSpecs() {
        return new String[]{"/swagger/swagger.json", "/swagger/swagger.yaml"};
    }

    @Override
    public Servlet getServlet() {
        if (!conf.getBoolean(PROP_ENABLE, false))
            return null;
        return this;
    }
}
