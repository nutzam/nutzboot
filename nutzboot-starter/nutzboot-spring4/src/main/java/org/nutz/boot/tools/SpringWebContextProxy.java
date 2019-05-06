package org.nutz.boot.tools;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.nutz.boot.AppContext;
import org.nutz.boot.starter.WebEventListenerFace;
import org.nutz.integration.spring.SpringIocLoader2;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * Spring Mvc依然是Mvc市场的老大,很多第三方框架需要使用SpringMvc的Ioc容器,这里创建的桥接器,可以模拟Spring环境.
 * <p/>
 * 子类是IocBean对象哦,会作为ServletContextListener,进入Servlet容器的初始化过程
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public abstract class SpringWebContextProxy implements ServletContextListener, WebEventListenerFace {

    /**
     * Nutz的Ioc容器
     */
    @Inject("refer:$ioc")
    protected Ioc ioc;

    /**
     * 全局上下文
     */
    @Inject
    protected AppContext appContext;

    /**
     * Spring Mvc的Ioc容器
     */
    protected XmlWebApplicationContext applicationContext;

    /**
     * 配置文件路径,供子类赋值
     */
    protected String configLocation;
    /**
     * 供子类赋值,应全局唯一
     */
    protected String selfName;

    /**
     * 容器初始化时,初始化Spring Mvc的Ioc容器,并放入ServletContext
     */
    public void contextInitialized(ServletContextEvent sce) {
        applicationContext = new XmlWebApplicationContext();
        applicationContext.setServletContext(sce.getServletContext());
        applicationContext.setConfigLocation(configLocation);
        applicationContext.refresh();
        appContext.getComboIocLoader().addLoader(new SpringIocLoader2(applicationContext, getSpringBeanNames().toArray(new String[0])));
        sce.getServletContext().setAttribute("spring." + selfName, applicationContext);
        // 登记所有标注了@AsSpringBean的对象到spring ioc
        Ioc ioc = appContext.getIoc();
        for (String name : ioc.getNamesByAnnotation(AsSpringBean.class)) {
            applicationContext.getBeanFactory().registerSingleton(name, ioc.get(null, name));
        }
    }

    /**
     * 获取Spring Mvc中的对象名,供Nutz Ioc调用
     * 
     * @return Spring Mvc中的对象名列表
     */
    protected List<String> getSpringBeanNames() {
        List<String> names = new ArrayList<>();
        for (String name : applicationContext.getBeanDefinitionNames()) {
            if (name.startsWith(selfName + ".")) {
                names.add(name);
            }
        }
        return names;
    }

    /**
     * 销毁Spring Mvc Ioc
     */
    public void contextDestroyed(ServletContextEvent sce) {
        if (applicationContext != null)
            applicationContext.destroy();
    }

    /**
     * 供NutzBoot的web容器使用的
     */
    public EventListener getEventListener() {
        return this;
    }
}
