package org.nutz.boot.starter.uflo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
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
import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.IocContext;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.bstek.uflo.console.handler.ServletHandler;

@SuppressWarnings("serial")
@IocBean
public class UfloServletStarter extends HttpServlet implements WebServletFace {
    
    public static final String URL="/uflo";
    
    protected Map<String,ServletHandler> handlerMap = new HashMap<String,ServletHandler>();

    @Inject
    protected PropertiesProxy conf;

    @Inject
    protected AppContext appContext;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    protected XmlWebApplicationContext applicationContext;

    protected ContextLoaderListener ctx;

    public String getName() {
        return "uflo";
    }

    public String getPathSpec() {
        return "/uflo/*";
    }

    public Servlet getServlet() {
        return this;
    }

    public Map<String, String> getInitParameters() {
        return new HashMap<>();
    }

    public void init(ServletConfig config) throws ServletException {
        applicationContext = new XmlWebApplicationContext();
        applicationContext.setServletContext(config.getServletContext());
        applicationContext.setConfigLocation("classpath:uflo-spring-context.xml");
        applicationContext.refresh();
        IocContext ictx = ((Ioc2) ioc).getIocContext();
        for (String name : applicationContext.getBeanDefinitionNames()) {
            if (name.startsWith("uflo.")) {
                switch (name) {
                case "uflo.props":
                case "uflo.environmentProvider":
                    break;
                default:
                    Object bean = applicationContext.getBean(name);
                    ictx.save("app", name, new ObjectProxy(bean));
                    break;
                }
            }
        }
        Collection<ServletHandler> handlers = applicationContext.getBeansOfType(ServletHandler.class).values();
        for(ServletHandler handler:handlers){
            String url=handler.url();
            if(handlerMap.containsKey(url)){
                throw new RuntimeException("Handler ["+url+"] already exist.");
            }
            handlerMap.put(url, handler);
        }
    }
    
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            String path=req.getContextPath()+URL;
            String uri=req.getRequestURI();
            String targetUrl=uri.substring(path.length());
            if(targetUrl.length()<1){
                resp.sendRedirect(req.getContextPath()+"/uflo/todo");
                return;
            }
            int slashPos=targetUrl.indexOf("/",1);
            if(slashPos>-1){
                targetUrl=targetUrl.substring(0,slashPos);
            }
            ServletHandler targetHandler=handlerMap.get(targetUrl);
            if(targetHandler==null){
                outContent(resp,"Handler ["+targetUrl+"] not exist.");
                return;
            }
            targetHandler.execute(req, resp);
        }catch(Exception ex){
            Throwable e=getCause(ex);
            resp.setCharacterEncoding("UTF-8");
            PrintWriter pw=resp.getWriter();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorMsg = e.getMessage();
            if(StringUtils.isBlank(errorMsg)){
                errorMsg=e.getClass().getName();
            }
            pw.write(errorMsg);
            pw.close();
            throw new ServletException(ex);             
        }
    }
    
    protected Throwable getCause(Throwable e){
        if(e.getCause()!=null){
            return getCause(e.getCause());
        }
        return e;
    }
    
    protected void outContent(HttpServletResponse resp,String msg) throws IOException {
        resp.setContentType("text/html");
        PrintWriter pw=resp.getWriter();
        pw.write("<html>");
        pw.write("<header><title>Uflo Console</title></header>");
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
