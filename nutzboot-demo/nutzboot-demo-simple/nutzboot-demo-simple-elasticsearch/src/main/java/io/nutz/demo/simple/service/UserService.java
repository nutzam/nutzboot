package io.nutz.demo.simple.service;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import io.nutz.demo.simple.bean.User;
import io.nutz.demo.simple.page.Pagination;
import io.nutz.demo.simple.utils.ElasticsearchUtil;

/**
 * Created by wizzer on 2018/6/15.
 */
@IocBean(create = "init")
public class UserService {
    private final static Log log = Logs.get();
    @Inject
    private ElasticsearchUtil elasticsearchUtil;
    private static String indexName = "user";
    private static String indexType = "user";

    public void init() throws Exception {
        //创建索引
        if (!elasticsearchUtil.isExistsIndex(indexName))
            elasticsearchUtil.createIndex(indexName);
        if (!elasticsearchUtil.isExistsType(indexName, indexType)) {
            //初始化索引表
            XContentBuilder mapping = jsonBuilder().startObject()
                    .startObject("user")
                    .startObject("_all")//设置IK分词,请事先安装IK分词插件
                    .field("analyzer", "ik_max_word")
                    .field("search_analyzer", "ik_max_word")
                    .field("term_vector", "no")
                    .field("store", "false")
                    .endObject()
                    .startObject("properties")
                    //设置name字段使用分词器
                    .startObject("name").field("type", "text").field("analyzer", "ik_max_word").endObject()
                    .startObject("createAt").field("type", "long").endObject()
                    .endObject()
                    .endObject()
                    .endObject();
            elasticsearchUtil.putMapping(indexName, indexType, mapping);
        }
    }

    public boolean createOrUpdateData(User user) {
        return elasticsearchUtil.createOrUpdateData(indexName, indexType, user.getId(), user);
    }

    public boolean deleteData(String id) {
        return elasticsearchUtil.deleteData(indexName, indexType, id);
    }

    /**
     * 分页查询
     *
     * @param pageNumber 页码
     * @param pageSize   页大小
     * @param keyword    关键词
     * @param highlight  是否高亮
     * @param explain    是否按匹配度排序
     * @param sortName   排序字段
     * @param sortOrder  排序方式
     * @return
     */
    public Pagination listPage(int pageNumber, int pageSize, String keyword, boolean highlight, boolean explain, String sortName, String sortOrder) {
        Pagination page = new Pagination();
        page.setPageNo(pageNumber);
        page.setPageSize(pageSize);
        try {
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            //根据名称查询
            if (Strings.isNotBlank(keyword)) {
                BoolQueryBuilder keywordQuery = QueryBuilders.boolQuery();
                keywordQuery.should(QueryBuilders.queryStringQuery(keyword).field("name", 7).field("email", 3));
                query.must(keywordQuery);
            }
            //日期起
//            if (Strings.isNotBlank(startDate)) {
//                query.must(QueryBuilders.rangeQuery("createAt").gte(DateUtil.getTime(startDate + " 00:00:00")));
//            }
            //日期至
//            if (Strings.isNotBlank(endDate)) {
//                query.must(QueryBuilders.rangeQuery("createAt").lte(DateUtil.getTime(endDate + " 23:59:59")));
//            }
            SearchRequestBuilder srb = elasticsearchUtil.getClient().prepareSearch(indexName)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setTypes(indexType)
                    .setQuery(query)
                    //分页
                    .setFrom((pageNumber - 1) * pageSize).setSize(pageSize)
                    //是否按匹配度排序
                    .setExplain(explain);

            if (highlight) {
                HighlightBuilder highlightBuilder = new HighlightBuilder().field("*").requireFieldMatch(false);
                highlightBuilder.preTags("<span style=\"color:red\">");
                highlightBuilder.postTags("</span>");
                srb.highlighter(highlightBuilder);
            }
//            if (Strings.isNotBlank(sortName)&!explain) {
//                String[] sortNames = StringUtils.split(sortName, ",");
//                if ("asc".equalsIgnoreCase(sortOrder)) {
//                    for (String s : sortNames) {
//                        srb.addSort(s, SortOrder.ASC);
//                    }
//                } else {
//                    for (String s : sortNames) {
//                        srb.addSort(s, SortOrder.DESC);
//                    }
//                }
//            }
            log.debug("srb:::\r\n" + srb.toString());
            SearchResponse response = srb.execute().actionGet();
            SearchHits hits = response.getHits();
            page.setTotalCount(hits.getHits().length);
            List<Map<String, Object>> list = new ArrayList<>();
            hits.forEach(searchHit -> {
                Map<String, Object> source = searchHit.getSourceAsMap();
                Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
                //name高亮
                HighlightField nameField = highlightFields.get("name");
                if (nameField != null) {
                    Text[] fragments = nameField.fragments();
                    String tmp = "";
                    for (Text text : fragments) {
                        tmp += text;
                    }
                    source.put("name", tmp);
                }
                list.add(source);
            });
            page.setList(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return page;
    }

    public Map<String, Object> getUser(String id) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.matchQuery("id", id));
        SearchRequestBuilder srb = elasticsearchUtil.getClient().prepareSearch(indexName)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setTypes(indexType)
                .setQuery(query);
        log.debug("srb:::\r\n" + srb.toString());
        SearchResponse response = srb.execute().actionGet();
        SearchHits hits = response.getHits();
        if (hits.getHits().length > 0)
            return hits.getAt(0).getSourceAsMap();
        return null;
    }
}
