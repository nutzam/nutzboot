package org.nutz.boot.starter.elasticsearch.rest;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 王庆华 on 2021/8/28.
 */
@IocBean
public class ElasticsearchStarter implements ServerFace {
    private final static Log log = Logs.get();

    @Inject("refer:$ioc")
    protected Ioc ioc;
    @Inject
    protected PropertiesProxy conf;

    protected static final String PRE = "elasticsearch.";

    @PropDoc(value = "Elasticsearch的主机地址", defaultValue = "127.0.0.1:9200", need = true)
    public static final String PROP_HOST = PRE + "host";

    public void start() throws Exception {
        ioc.get(RestHighLevelClient.class, "elasticsearchClient");
    }

    @IocBean(name = "elasticsearchClient", depose = "close")
    public RestHighLevelClient getElasticsearchClient() {
        log.debug("loading elasticsearchClient...");

        final String hosts_str = conf.get(PROP_HOST, "127.0.0.1:9200");
        List<HttpHost> httpHostList = new ArrayList<>();
        if (hosts_str.indexOf(",") != -1) {
            for (String ht : hosts_str.split(",")) {
                httpHostList.add(new HttpHost(ht.split(":")[0], Integer.valueOf(ht.split(":")[1])));
            }
        } else {
            httpHostList.add(new HttpHost(hosts_str.split(":")[0], Integer.valueOf(hosts_str.split(":")[1])));
        }

        HttpHost[] httpHosts = new HttpHost[httpHostList.size()];
        for (int i = 0; i < httpHostList.size(); i++) {
            httpHosts[i] = httpHostList.get(i);
        }
        final RestClientBuilder builder = RestClient.builder(httpHosts);
        return new RestHighLevelClient(builder);
    }
}
