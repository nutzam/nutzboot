package org.nutz.boot.starter.eureka.client;

import java.util.Iterator;

import javax.inject.Provider;

import org.apache.commons.configuration.AbstractConfiguration;
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

    public void setConf(PropertiesProxy conf) {
        this.conf = conf;
        if (!conf.has("eureka.port")) {
            conf.put("eureka.port", conf.get("server.port", conf.get("jetty.port", conf.get("undertow.port", conf.get("tomcat.port")))));
        }
        if (!conf.has("eureka.name")) {
            conf.put("eureka.name", conf.check("nutz.application.name"));
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
                return getHostName(false) + "-" +getNonSecurePort();
            return instanceId;
        }
        
    }
}
