package org.nutz.boot.starter.eureka.server;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.boot.starter.WebServletFace;
import org.nutz.conf.NutConf;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.util.NutMap;

import com.netflix.appinfo.AmazonInfo;
import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.config.ConfigurationManager;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Pair;
import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.EurekaServerContextHolder;
import com.netflix.eureka.cluster.PeerEurekaNode;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.registry.PeerAwareInstanceRegistryImpl;
import com.netflix.eureka.resources.StatusResource;
import com.netflix.eureka.util.StatusInfo;

@SuppressWarnings("serial")
@IocBean
public class EurekaServletStarter extends HttpServlet implements WebServletFace {

    @Override
    public String getName() {
        return "eureka";
    }

    @Override
    public String getPathSpec() {
        return "/action/status.json";
    }

    @Override
    public Servlet getServlet() {
        return this;
    }

    public void init(ServletConfig config) {
        NutConf.USE_FASTCLASS = false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final String pathInfo = request.getRequestURI();

        NutMap result = NutMap.NEW();

        result.put("basePath",
                   request.getScheme()
                               + "://"
                               + request.getServerName()
                               + ":"
                               + request.getServerPort()
                               + request.getContextPath()
                               + "/");

        populateBase(result);

        if (pathInfo.endsWith("/status.json")) {
            status(result);
            lastn(result);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(Json.toJson(result, JsonFormat.tidy()));
        } else {
            response.setStatus(404);
        }
    }

    private void status(NutMap result) {
        populateApps(result);

        StatusInfo statusInfo;
        try {
            statusInfo = new StatusResource().getStatusInfo();
        }
        catch (Exception e) {
            statusInfo = StatusInfo.Builder.newBuilder().isHealthy(false).build();
        }

        result.put("statusInfo", statusInfo);
        populateInstanceInfo(result, statusInfo);
        filterReplicas(result, statusInfo);
    }

    private void lastn(NutMap result) {
        PeerAwareInstanceRegistryImpl registry = (PeerAwareInstanceRegistryImpl) getRegistry();
        List<NutMap> lastNCanceled = registry.getLastNCanceledInstances()
                                             .stream()
                                             .map(entry -> NutMap.NEW()
                                                                 .setv("id", entry.second())
                                                                 .setv("date",
                                                                       new Date(entry.first())))
                                             .collect(Collectors.toList());
        result.put("lastNCanceled", lastNCanceled);

        List<NutMap> lastNRegistered = registry.getLastNRegisteredInstances()
                                               .stream()
                                               .map(entry -> NutMap.NEW()
                                                                   .setv("id", entry.second())
                                                                   .setv("date",
                                                                         new Date(entry.first())))
                                               .collect(Collectors.toList());
        result.put("lastNRegistered", lastNRegistered);
    }

    private PeerAwareInstanceRegistry getRegistry() {
        return getServerContext().getRegistry();
    }

    private EurekaServerContext getServerContext() {
        return EurekaServerContextHolder.getInstance().getServerContext();
    }

    private void populateBase(NutMap result) {
        result.put("time", new Date());
        populateHeader(result);
        populateNavbar(result);
    }

    private void populateNavbar(NutMap result) {
        List<NutMap> replicas = new ArrayList<>();
        List<PeerEurekaNode> list = getServerContext().getPeerEurekaNodes().getPeerNodesView();
        for (PeerEurekaNode node : list) {
            try {
                URI uri = new URI(node.getServiceUrl());
                String href = scrubBasicAuth(node.getServiceUrl());
                replicas.add(NutMap.NEW().setv("key", uri.getHost()).setv("value", href));
            }
            catch (Exception ex) {}
        }
        result.put("replicas", replicas);
    }

    private void populateHeader(NutMap result) {
        result.put("currentTime", StatusResource.getCurrentTimeAsString());
        result.put("upTime", StatusInfo.getUpTime());
        result.put("environment",
                   ConfigurationManager.getDeploymentContext().getDeploymentEnvironment());
        result.put("datacenter",
                   ConfigurationManager.getDeploymentContext().getDeploymentDatacenter());
        PeerAwareInstanceRegistry registry = getRegistry();
        NutMap registryMap = NutMap.NEW()
                                   .setv("leaseExpirationEnabled",
                                         registry.isLeaseExpirationEnabled())
                                   .setv("numOfRenewsPerMinThreshold",
                                         registry.getNumOfRenewsPerMinThreshold())
                                   .setv("numOfRenewsInLastMin", registry.getNumOfRenewsInLastMin())
                                   .setv("selfPreservationModeEnabled",
                                         registry.isSelfPreservationModeEnabled());
        result.put("registry", registryMap);
        result.put("isBelowRenewThresold", registry.isBelowRenewThresold() == 1);
        DataCenterInfo info = getServerContext().getApplicationInfoManager()
                                                .getInfo()
                                                .getDataCenterInfo();
        result.put("amazonInfo", false);
        if (info.getName() == DataCenterInfo.Name.Amazon) {
            AmazonInfo amazonInfo = (AmazonInfo) info;
            result.put("amazonInfo", true);
            result.put("amiId", amazonInfo.get(AmazonInfo.MetaDataKey.amiId));
            result.put("availabilityZone", amazonInfo.get(AmazonInfo.MetaDataKey.availabilityZone));
            result.put("instanceId", amazonInfo.get(AmazonInfo.MetaDataKey.instanceId));
        }
    }

    private void populateApps(NutMap result) {
        List<Application> sortedApplications = getRegistry().getSortedApplications();
        ArrayList<Map<String, Object>> apps = new ArrayList<>();
        for (Application app : sortedApplications) {
            LinkedHashMap<String, Object> appData = new LinkedHashMap<>();
            apps.add(appData);
            appData.put("name", app.getName());
            Map<String, Integer> amiCounts = new HashMap<>();
            Map<InstanceInfo.InstanceStatus, List<Pair<String, String>>> instancesByStatus = new HashMap<>();
            Map<String, Integer> zoneCounts = new HashMap<>();
            for (InstanceInfo info : app.getInstances()) {
                String id = info.getId();
                String url = info.getStatusPageUrl();
                InstanceInfo.InstanceStatus status = info.getStatus();
                String ami = "n/a";
                String zone = "";
                if (info.getDataCenterInfo().getName() == DataCenterInfo.Name.Amazon) {
                    AmazonInfo dcInfo = (AmazonInfo) info.getDataCenterInfo();
                    ami = dcInfo.get(AmazonInfo.MetaDataKey.amiId);
                    zone = dcInfo.get(AmazonInfo.MetaDataKey.availabilityZone);
                }
                Integer count = amiCounts.get(ami);
                if (count != null) {
                    amiCounts.put(ami, count + 1);
                } else {
                    amiCounts.put(ami, 1);
                }
                count = zoneCounts.get(zone);
                if (count != null) {
                    zoneCounts.put(zone, count + 1);
                } else {
                    zoneCounts.put(zone, 1);
                }
                List<Pair<String, String>> list = instancesByStatus.get(status);
                if (list == null) {
                    list = new ArrayList<>();
                    instancesByStatus.put(status, list);
                }
                list.add(new Pair<>(id, url));
            }
            appData.put("amiCounts",
                        amiCounts.entrySet()
                                 .stream()
                                 .map(map -> NutMap.NEW()
                                                   .setv("key", map.getKey())
                                                   .setv("value", map.getValue()))
                                 .collect(Collectors.toList()));
            appData.put("zoneCounts",
                        zoneCounts.entrySet()
                                  .stream()
                                  .map(map -> NutMap.NEW()
                                                    .setv("key", map.getKey())
                                                    .setv("value", map.getValue()))
                                  .collect(Collectors.toList()));
            ArrayList<Map<String, Object>> instanceInfos = new ArrayList<>();
            appData.put("instanceInfos", instanceInfos);

            instancesByStatus.forEach((key, value) -> {
                LinkedHashMap<String, Object> instanceData = new LinkedHashMap<>();
                instanceInfos.add(instanceData);
                instanceData.put("status", key);
                ArrayList<Map<String, Object>> instances = new ArrayList<>();
                instanceData.put("instances", instances);
                instanceData.put("isNotUp", key != InstanceInfo.InstanceStatus.UP);

                for (Pair<String, String> p : value) {
                    LinkedHashMap<String, Object> instance = new LinkedHashMap<>();
                    instances.add(instance);
                    instance.put("id", p.first());
                    String url = p.second();
                    instance.put("url", url);
                    boolean isHref = url != null && url.startsWith("http");
                    instance.put("isHref", isHref);
                }
            });
        }
        result.put("apps", apps);
    }

    private void populateInstanceInfo(NutMap result, StatusInfo statusInfo) {
        InstanceInfo instanceInfo = statusInfo.getInstanceInfo();
        Map<String, String> instanceMap = new HashMap<>();
        instanceMap.put("ipAddr", instanceInfo.getIPAddr());
        instanceMap.put("status", instanceInfo.getStatus().toString());
        if (instanceInfo.getDataCenterInfo().getName() == DataCenterInfo.Name.Amazon) {
            AmazonInfo info = (AmazonInfo) instanceInfo.getDataCenterInfo();
            instanceMap.put("availability-zone", info.get(AmazonInfo.MetaDataKey.availabilityZone));
            instanceMap.put("public-ipv4", info.get(AmazonInfo.MetaDataKey.publicIpv4));
            instanceMap.put("instance-id", info.get(AmazonInfo.MetaDataKey.instanceId));
            instanceMap.put("public-hostname", info.get(AmazonInfo.MetaDataKey.publicHostname));
            instanceMap.put("ami-id", info.get(AmazonInfo.MetaDataKey.amiId));
            instanceMap.put("instance-type", info.get(AmazonInfo.MetaDataKey.instanceType));
        }
        result.put("instanceInfo", instanceMap);
    }

    private void filterReplicas(NutMap result, StatusInfo statusInfo) {
        Map<String, String> applicationStats = statusInfo.getApplicationStats();
        if (applicationStats.get("registered-replicas").contains("@")) {
            applicationStats.put("registered-replicas",
                                 scrubBasicAuth(applicationStats.get("registered-replicas")));
        }
        if (applicationStats.get("unavailable-replicas").contains("@")) {
            applicationStats.put("unavailable-replicas",
                                 scrubBasicAuth(applicationStats.get("unavailable-replicas")));
        }
        if (applicationStats.get("available-replicas").contains("@")) {
            applicationStats.put("available-replicas",
                                 scrubBasicAuth(applicationStats.get("available-replicas")));
        }
        result.put("applicationStats", applicationStats);
    }

    private String scrubBasicAuth(String urlList) {
        String[] urls = urlList.split(",");
        StringBuilder filteredUrls = new StringBuilder();
        for (String u : urls) {
            if (u.contains("@")) {
                filteredUrls.append(u.substring(0, u.indexOf("//") + 2))
                            .append(u.substring(u.indexOf("@") + 1, u.length()))
                            .append(",");
            } else {
                filteredUrls.append(u).append(",");
            }
        }
        return filteredUrls.substring(0, filteredUrls.length() - 1);
    }
}
