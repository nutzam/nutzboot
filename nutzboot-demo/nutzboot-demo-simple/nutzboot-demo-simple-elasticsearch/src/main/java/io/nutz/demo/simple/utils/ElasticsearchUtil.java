package io.nutz.demo.simple.utils;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;

import java.util.List;

/**
 * Created by wizzer on 2018/6/15.
 */
@IocBean
public class ElasticsearchUtil {
    @Inject
    private TransportClient client;

    public TransportClient getClient() {
        return client;
    }

    /**
     * 判断指定的索引是否存在
     *
     * @param indexName 索引名
     * @return true/false
     */
    public boolean isExistsIndex(String indexName) {
        IndicesExistsResponse response =
                getClient().admin().indices().exists(
                        new IndicesExistsRequest().indices(new String[]{indexName})).actionGet();
        return response.isExists();
    }

    /**
     * 判断指定的索引的类型是否存在
     *
     * @param indexName 索引名
     * @param indexType 索引类型
     * @return true/false
     */
    public boolean isExistsType(String indexName, String indexType) {
        TypesExistsResponse response =
                getClient().admin().indices()
                        .typesExists(new TypesExistsRequest(new String[]{indexName}, indexType)
                        ).actionGet();
        return response.isExists();
    }

    /**
     * 删除索引
     *
     * @param indexName 索引名
     * @return true/false
     */
    public boolean deleteIndex(String indexName) {
        DeleteIndexResponse response =
                getClient().admin().indices().prepareDelete(indexName).execute().actionGet();
        return response.isAcknowledged();
    }

    /**
     * 创建索引
     *
     * @param indexName 索引名
     * @return true/false
     */
    public boolean createIndex(String indexName) {
        CreateIndexResponse response =
                getClient().admin().indices().prepareCreate(indexName).execute().actionGet();
        return response.isAcknowledged();
    }

    /**
     * @param indexName 索引名
     * @param type      数据类型(表名)
     * @param mapping   mapping对象
     */
    public boolean putMapping(String indexName, String type, XContentBuilder mapping) {
        PutMappingRequest mappingRequest = Requests.putMappingRequest(indexName).type(type).source(mapping);
        PutMappingResponse response = getClient().admin().indices().putMapping(mappingRequest).actionGet();
        return response.isAcknowledged();
    }

    /**
     * 创建或更新文档
     *
     * @param indexName 索引名
     * @param type      数据类型(表名)
     * @param id        主键
     * @param obj       对象
     * @return
     */
    public boolean createOrUpdateData(String indexName, String type, String id, Object obj) {
        IndexResponse response = getClient().prepareIndex(indexName, type).setId(id).setSource(Lang.obj2map(obj)).execute().actionGet();
        return response.getResult() == DocWriteResponse.Result.CREATED || response.getResult() == DocWriteResponse.Result.UPDATED;
    }

    /**
     * 创建或更新文档
     *
     * @param indexName 索引名
     * @param type      数据类型
     * @param id        主键
     * @return
     */
    public boolean deleteData(String indexName, String type, String id) {
        DeleteResponse response = getClient().prepareDelete().setIndex(indexName).setType(type).setId(id).execute().actionGet();
        return response.getResult() == DocWriteResponse.Result.DELETED;
    }

    /**
     * 批量创建或更新文档
     *
     * @param indexName 索引名
     * @param type      数据类型(表名)
     * @param list      包含id和obj的NutMap集合对象
     */
    public void createOrUpdateData(String indexName, String type, List<NutMap> list) {
        BulkRequestBuilder bulkRequest = getClient().prepareBulk();
        if (list.size() > 0) {
            for (NutMap nutMap : list) {
                bulkRequest.add(getClient().prepareIndex(indexName, type).setId(nutMap.getString("id")).setSource(Lang.obj2map(nutMap.get("obj"))));
            }
            bulkRequest.execute().actionGet();
        }
    }
}
