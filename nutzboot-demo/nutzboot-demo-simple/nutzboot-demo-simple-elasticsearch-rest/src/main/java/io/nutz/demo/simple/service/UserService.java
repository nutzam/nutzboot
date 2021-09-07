package io.nutz.demo.simple.service;

import io.nutz.demo.simple.bean.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@IocBean(create = "init")
public class UserService {
    private final static Log log = Logs.get();

    @Inject
    private RestHighLevelClient client;

    private String index = User.class.getSimpleName().toLowerCase();

    public void init() throws Exception {
        boolean exists = client.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
        if (!exists) {
            createIndex();
        }
    }

    public void createIndex() throws IOException {
        final CreateIndexResponse createIndexResponse;
        createIndexResponse = client.indices().create(new CreateIndexRequest(index), RequestOptions.DEFAULT);
        log.debug("createIndex ack:" + createIndexResponse.isAcknowledged());
    }

    public void deleteIndex() throws IOException {
        final AcknowledgedResponse response = client.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
        log.debug("deleteIndex ack:" + response.isAcknowledged());
    }

    public boolean addUser(User user) throws IOException {
        final IndexRequest indexRequest = new IndexRequest();
        indexRequest.index(index)
                .id(user.getId() != null ? user.getId() : null);

        indexRequest.source(Json.toJson(user), XContentType.JSON);
        IndexResponse index = client.index(indexRequest, RequestOptions.DEFAULT);
        log.debug("addUser result:" + index.getResult());
        return index.getResult().name().equals("CREATED");
    }

    public void batchInsert(List<User> users) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        users.forEach(user -> {
            bulkRequest.add(new IndexRequest().index(index).id(user.getId() != null ? user.getId() : null)
                    .source(Json.toJson(user), XContentType.JSON));
        });
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        log.debug(Json.toJson(bulkResponse.getItems()));
    }

    public boolean deleteUser(String userId) throws IOException {
        final DeleteResponse response = client.delete(new DeleteRequest(index, userId), RequestOptions.DEFAULT);
        return response.getResult().name().equals("DELETED");
    }

    public void batchDeleteUser(String[] userIds) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (String userId : userIds) {
            bulkRequest.add(new DeleteRequest().index(index).id(userId));
        }

        final BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        log.debug(Json.toJson(bulkResponse.getItems()));
    }


    public boolean updateUser(User user) throws IOException {
        UpdateRequest request = new UpdateRequest();
        request.index(index).id(user.getId());
        request.doc(new IndexRequest().source(Json.toJson(user), XContentType.JSON));

        final UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        log.debug("updateUser:" + response.getResult());
        return response.getResult().name().equals("UPDATED");
    }

    public User fetchUser(String userId) throws IOException {
        final GetResponse documentFields = client.get(new GetRequest(index, userId), RequestOptions.DEFAULT);
        return Json.fromJson(User.class, documentFields.getSourceAsString());
    }

    public List<User> query(String name, String email) throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices("user");
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        final BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.must(QueryBuilders.matchQuery("name", name))
                .must(QueryBuilders.matchQuery("email", email));

        sourceBuilder.query(boolQueryBuilder);
        request.source(sourceBuilder);

        final SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        final SearchHits hits = response.getHits();

        List<User> users = new ArrayList<>();
        for (SearchHit hit : hits) {
            users.add(Json.fromJson(User.class, hit.getSourceAsString()));
        }
        return users;
    }

    public List<User> queryPage(String name, Integer pageNumber, Integer pageSize) throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices(index);
        final SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(
                QueryBuilders.termQuery("name", name));

        sourceBuilder.from((pageNumber - 1) * pageSize);
        sourceBuilder.size(pageSize);
        request.source(sourceBuilder);

        final SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        final SearchHits hits = response.getHits();
        List<User> users = new ArrayList<>();
        for (SearchHit hit : hits) {
            users.add(Json.fromJson(User.class, hit.getSourceAsString()));
        }
        return users;
    }

}