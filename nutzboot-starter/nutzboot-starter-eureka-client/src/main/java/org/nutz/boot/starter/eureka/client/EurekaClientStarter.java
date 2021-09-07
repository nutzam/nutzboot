package org.nutz.boot.starter.eureka.client;

import java.util.Iterator;

import javax.inject.Provider;

import org.apache.commons.configuration.AbstractConfiguration;
import org.nutz.boot.AppContext;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;

@IocBean
public class EurekaClientStarter implements ServerFace, Provider<EurekaClient> {

    @Inject
    protected PropertiesProxy conf;
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    @Inject
    protected AppContext appContext;

    protected ApplicationInfoManager applicationInfoManager;
    
    protected DiscoveryClient eurekaClient;
    
    @IocBean(name="eurekaInstanceConfig")
    public EurekaInstanceConfig getEurekaInstanceConfig() {
        return new NbEurekaInstanceConfig();
    }
    
    @IocBean(name="applicationInfoManager")
    public ApplicationInfoManager getApplicationInfoManager(EurekaInstanceConfig eurekaInstanceConfig) {
        InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(eurekaInstanceConfig).get();
        ApplicationInfoManager applicationInfoManager = new ApplicationInfoManager(eurekaInstanceConfig, instanceInfo);
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.STARTING);
        return applicationInfoManager;
    }
    
    @IocBean(name="eurekaClient")
    public DiscoveryClient eurekaClient() {
        return new DiscoveryClient(applicationInfoManager, new DefaultEurekaClientConfig());
    }

    public void start() throws Exception {
        applicationInfoManager = ioc.get(ApplicationInfoManager.class);
        applicationInfoManager.setInstanceStatus(InstanceStatus.UP);
        eurekaClient = ioc.get(DiscoveryClient.class, "eurekaClient");
    }

    public void stop() throws Exception {
        if (applicationInfoManager != null)
            applicationInfoManager.setInstanceStatus(InstanceStatus.DOWN);
        if (eurekaClient != null)
            eurekaClient.shutdown();
    }

    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
        if (!conf.has("eureka.port")) {
            conf.put("eureka.port", ""+appContext.getServerPort("jetty.port"));
        }
        if (!conf.has("eureka.name")) {
            conf.put("eureka.name", appContext.getConf().check("nutz.application.name"));
        }
        DynamicPropertyFactory.initWithConfigurationSource(new XConfigure());
    }
    
    public class XConfigure extends AbstractConfiguration {

        public boolean isEmpty() {
            return false;
        }

        public boolean containsKey(String key) {
            return conf.containsKey(key);
        }

        public Object getProperty(String key) {
            return conf.get(key);
        }

        public Iterator<String> getKeys() {
            return conf.keys().iterator();
        }

        protected void addPropertyDirect(String key, Object value) {
            conf.put(key, key);
        }
        
    }

    public DiscoveryClient get() {
        return eurekaClient;
    }
    
    public class NbEurekaInstanceConfig extends MyDataCenterInstanceConfig {

        public String getInstanceId() {
            String instanceId = super.getInstanceId();
            if (Strings.isBlank(instanceId))
                return getIpAddress() + ":" + getVirtualHostName() + ":" +getNonSecurePort();
            return instanceId;
        }
        
        @Override
        public String getHostName(boolean refresh) {
            if (conf.has("server.hostname"))
                return conf.get("server.hostname");
            return super.getHostName(refresh);
        }
    }
}
