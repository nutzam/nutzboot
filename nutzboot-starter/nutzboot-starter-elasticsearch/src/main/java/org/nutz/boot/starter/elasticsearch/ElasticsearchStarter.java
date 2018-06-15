package org.nutz.boot.starter.elasticsearch;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.starter.ServerFace;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.net.InetAddress;

/**
 * Created by wizzer on 2018/6/15.
 */
@IocBean
public class ElasticsearchStarter implements ServerFace {

    private final static Log log = Logs.get();
    @Inject("refer:$ioc")
    protected Ioc ioc;
    @Inject
    protected PropertiesProxy conf;

    protected TransportClient client;

    protected static final String PRE = "elasticsearch.";

    @PropDoc(value = "Elasticsearch的ip地址", defaultValue = "127.0.0.1", need = true)
    public static final String PROP_HOST = PRE + "host";

    @PropDoc(value = "Elasticsearch的端口", defaultValue = "9300", type = "int", need = true)
    public static final String PROP_PORT = PRE + "port";

    @PropDoc(value = "Elasticsearch的集群名称", defaultValue = "elasticsearch", need = true)
    public static final String PROP_CLUSTER_NAME = PRE + "cluster.name";

    @PropDoc(value = "Elasticsearch是否嗅探集群状态", defaultValue = "true", type = "boolean", need = true)
    public static final String PROP_CLIENT_TRANSPORT_SNIFF = PRE + "client.transport.sniff";

    @Override
    public void start() throws Exception {
        if (client == null) {
            Settings esSettings = Settings.builder()
                    .put("cluster.name", conf.get(PROP_CLUSTER_NAME))
                    //设置ES实例的名称
                    .put("client.transport.sniff", conf.getBoolean(PROP_CLIENT_TRANSPORT_SNIFF))
                    //自动嗅探整个集群的状态，把集群中其他ES节点的ip添加到本地的客户端列表中
                    .build();
            client = new PreBuiltTransportClient(esSettings);
            client.addTransportAddress(new TransportAddress(InetAddress.getByName(conf.get(PROP_HOST)), conf.getInt(PROP_PORT)));
        }
    }

    @IocBean(name = "elasticsearchClient")
    public TransportClient getElasticsearchClient() {
        return client;
    }

    @Override
    public void stop() throws Exception {
        if (client != null) {
            client.close();
        }
    }
}
