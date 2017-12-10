package org.nutz.boot.starter.urule;

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
import org.nutz.lang.Files;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.bstek.urule.console.exception.NoPermissionException;
import com.bstek.urule.console.repository.NodeLockException;
import com.bstek.urule.console.servlet.RequestHolder;
import com.bstek.urule.console.servlet.ServletHandler;

@SuppressWarnings("serial")
@IocBean
public class UruleServletStarter extends HttpServlet implements WebServletFace {
    
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
        return "urule";
    }

    public String getPathSpec() {
        return "/urule/*";
    }

    public Servlet getServlet() {
        return this;
    }

    public Map<String, String> getInitParameters() {
        return new HashMap<>();
    }

    public void init(ServletConfig config) throws ServletException {
        Files.createDirIfNoExists(conf.check("urule.repository.dir"));
        applicationContext = new XmlWebApplicationContext();
        applicationContext.setServletContext(config.getServletContext());
        applicationContext.setConfigLocation("classpath:urule-spring-context.xml");
        applicationContext.refresh();
        IocContext ictx = ((Ioc2) ioc).getIocContext();
        for (String name : applicationContext.getBeanDefinitionNames()) {
            if (name.startsWith("urule.")) {
                switch (name) {
                case "urule.props":
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
    public static final String URL="/urule";
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestHolder.set(req, resp);
        try{
            String path=req.getContextPath()+URL;
            String uri=req.getRequestURI();
            String targetUrl=uri.substring(path.length());
            if(targetUrl.length()<1){
                resp.sendRedirect(req.getContextPath()+"/urule/frame");
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
            if(e instanceof NoPermissionException){
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                pw.write("<h1>Permission denied!</h1>");
                pw.close();
            }else{
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                String errorMsg = e.getMessage();
                if(StringUtils.isBlank(errorMsg)){
                    errorMsg=e.getClass().getName();
                }
                pw.write(errorMsg);
                pw.close();
                if(!(e instanceof NodeLockException)){                  
                    throw new ServletException(ex);             
                }
            }
        }finally{
            RequestHolder.reset();
        }
    }

    private void outContent(HttpServletResponse resp,String msg) throws IOException {
        resp.setContentType("text/html");
        PrintWriter pw=resp.getWriter();
        pw.write("<html>");
        pw.write("<header><title>URule Console</title></header>");
        pw.write("<body>");
        pw.write(msg);
        pw.write("</body>");
        pw.write("</html>");
        pw.flush();
        pw.close();
    }
    
    private Throwable getCause(Throwable e){
        if(e.getCause()!=null){
            return getCause(e.getCause());
        }
        return e;
    }

    @Override
    public void destroy() {
        applicationContext.destroy();
    }
}
