package org.nutz.boot.starter.webjars;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;

@IocBean
public class WebjarsServletStarter extends HttpServlet implements WebServletFace {

    private static final long serialVersionUID = 5361494908208769417L;

    protected static final String PRE = "webjars.";

    @PropDoc(value = "是否关闭 webjars 的缓存", defaultValue = "false", type = "boolean")
    public static final String PROP_DISABLE_CACHE = PRE + "disableCache";

    @Inject
    protected PropertiesProxy conf;

    protected boolean disableCache;

    @Override
    public void init() {
        disableCache = conf.getBoolean(PROP_DISABLE_CACHE, false);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String webjarsResourceURI = "/META-INF/resources"
                                    + request.getRequestURI()
                                             .replaceFirst(request.getContextPath(), "");

        if (webjarsResourceURI.endsWith("/")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String[] tokens = webjarsResourceURI.split("/");

        // webjars 的 resource 不是官方提供的，直接返回 404
        if (tokens.length < 7) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String fileName = tokens[tokens.length - 1];
        String version = tokens[5];

        String eTagName = fileName + "_" + version;

        if (!disableCache) {
            if (checkETagMatch(request, eTagName) || checkLastModify(request)) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
        }

        try (InputStream inputStream = WebjarsServletStarter.class.getResourceAsStream(webjarsResourceURI)) {
            if (inputStream != null) {
                if (!disableCache) {
                    prepareCacheHeaders(response, eTagName);
                }

                String mimeType = this.getServletContext().getMimeType(fileName);

                response.setContentType(mimeType != null ? mimeType : "application/octet-stream");
                Streams.write(response.getOutputStream(), inputStream);
            } else {
                // 指定资源不存在，返回 404
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    @Override
    public String getName() {
        return "webjarsServlet";
    }

    @Override
    public int getLoadOnStartup() {
        return 2;
    }

    @Override
    public String getPathSpec() {
        return "/webjars/*";
    }

    @Override
    public Servlet getServlet() {
        return this;
    }

    private boolean checkETagMatch(HttpServletRequest request, String eTagName) {
        String token = request.getHeader("If-None-Match");
        return (token != null && token.equals(eTagName));
    }

    private boolean checkLastModify(HttpServletRequest request) {
        long last = request.getDateHeader("If-Modified-Since");
        return (last != -1L && (last - System.currentTimeMillis() > 0L));
    }

    // 1天的毫秒数
    private static final long DEFAULT_EXPIRE_TIME_MS = 86_400_000L;

    // 1天的秒数
    private static final long DEFAULT_EXPIRE_TIME_S = 86_400L;

    private void prepareCacheHeaders(HttpServletResponse response, String eTag) {
        response.setHeader("ETag", eTag);
        response.setDateHeader("Expires", System.currentTimeMillis() + DEFAULT_EXPIRE_TIME_MS);
        response.addDateHeader("Last-Modified",
                               System.currentTimeMillis() + DEFAULT_EXPIRE_TIME_MS);
        response.addHeader("Cache-Control", "private, max-age=" + DEFAULT_EXPIRE_TIME_S);
    }
}
