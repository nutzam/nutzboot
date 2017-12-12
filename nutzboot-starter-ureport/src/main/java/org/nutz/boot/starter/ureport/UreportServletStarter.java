package org.nutz.boot.starter.ureport;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.nutz.boot.AppContext;
import org.nutz.boot.starter.WebServletFace;
import org.nutz.integration.spring.SpringIocLoader2;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.bstek.ureport.console.RequestHolder;
import com.bstek.ureport.console.ServletAction;

@SuppressWarnings("serial")
@IocBean
public class UreportServletStarter extends HttpServlet implements WebServletFace {
    
    protected Map<String, ServletAction> handlerMap = new HashMap<String,ServletAction>();

    @Inject
    protected PropertiesProxy conf;

    @Inject
    protected AppContext appContext;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    protected XmlWebApplicationContext applicationContext;

    protected ContextLoaderListener ctx;

    public String getName() {
        return "ureport";
    }

    public String getPathSpec() {
        return "/ureport/*";
    }

    public Servlet getServlet() {
        return this;
    }

    public Map<String, String> getInitParameters() {
        return new HashMap<>();
    }

    public void init(ServletConfig config) throws ServletException {
        Files.createDirIfNoExists(conf.check("ureport.repository.dir"));
        applicationContext = new XmlWebApplicationContext();
        applicationContext.setServletContext(config.getServletContext());
        applicationContext.setConfigLocation("classpath:ureport-spring-context.xml");
        applicationContext.refresh();
        List<String> names = new ArrayList<>();
        for (String name : applicationContext.getBeanDefinitionNames()) {
            if (name.startsWith("ureport.")) {
                switch (name) {
                case "ureport.props":
                    break;
                default:
                    names.add(name);
                }
            }
        }
        appContext.getComboIocLoader().addLoader(new SpringIocLoader2(applicationContext, names.toArray(new String[names.size()])));
        Collection<ServletAction> handlers = applicationContext.getBeansOfType(ServletAction.class).values();
        for(ServletAction handler:handlers){
            String url=handler.url();
            if(handlerMap.containsKey(url)){
                throw new RuntimeException("Handler ["+url+"] already exist.");
            }
            handlerMap.put(url, handler);
        }
    }
    public static final String URL="/ureport";
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getContextPath() + URL;
        String uri = req.getRequestURI();
        String targetUrl = uri.substring(path.length());
        if (targetUrl.length() < 1) {
            outContent(resp, "Welcome to use ureport,please specify target url.");
            return;
        }
        int slashPos = targetUrl.indexOf("/", 1);
        if (slashPos > -1) {
            targetUrl = targetUrl.substring(0, slashPos);
        }
        ServletAction targetHandler = handlerMap.get(targetUrl);
        if (targetHandler == null) {
            outContent(resp, "Handler [" + targetUrl + "] not exist.");
            return;
        }
        RequestHolder.setRequest(req);
        try{
            targetHandler.execute(req, resp);
        }catch(Exception ex){
            resp.setCharacterEncoding("UTF-8");
            PrintWriter pw=resp.getWriter();
            Throwable e=buildRootException(ex);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorMsg = e.getMessage();
            if(StringUtils.isBlank(errorMsg)){
                errorMsg=e.getClass().getName();
            }
            pw.write(errorMsg);
            pw.close();             
            throw new ServletException(ex); 
        }finally{
            RequestHolder.clean();
        }
    }
    private Throwable buildRootException(Throwable throwable){
        if(throwable.getCause()==null){
            return throwable;
        }
        return buildRootException(throwable.getCause());
    }

    private void outContent(HttpServletResponse resp, String msg) throws IOException {
        resp.setContentType("text/html");
        PrintWriter pw = resp.getWriter();
        pw.write("<html>");
        pw.write("<header><title>UReport Console</title></header>");
        pw.write("<body>");
        pw.write(msg);
        pw.write("</body>");
        pw.write("</html>");
        pw.flush();
        pw.close();
    }

    @Override
    public void destroy() {
        applicationContext.destroy();
    }
}
