package io.nutz.demo.simple.controller;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.nutz.boot.starter.mongodb.plus.ZMongoDatabase;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@At("/device")
@IocBean
public class DeviceController {
    private static final Log log = Logs.get();

    @Inject
    private ZMongoDatabase zMongoDatabase;

    // http://127.0.0.1:8080/device/count
    @Ok("raw")
    @At("/count")
    public long count() {
        return zMongoDatabase.getCollection("device").countDocuments();
    }

    // http://127.0.0.1:8080/device/query
    @Ok("json:full")
    @At("/query")
    public Object query(@Param("..") Pager pager) {
        return zMongoDatabase.getCollection("device").find().limit(pager.getPageSize());
    }

    // http://127.0.0.1:8080/device/find
    @Ok("json:full")
    @At("/find")
    public Object find(@Param(value = "no", df = "0002") String no) {
        List<Bson> bsons = new ArrayList<>();
        Bson match = Aggregates.match(Filters.eq("metadata.no", no));
        Bson limit = Aggregates.limit(5);
        bsons.add(match);
        bsons.add(limit);
        log.info(Json.toJson(bsons));
        return zMongoDatabase.getCollection("device").aggregate(bsons);
    }

    // http://127.0.0.1:8080/device/avg
    // 官方时序数据统计Demo https://docs.mongodb.com/manual/core/timeseries-collections/
    @Ok("json:full")
    @At("/avg")
    public Object avg(@Param(value = "no", df = "0002") String no) {
        List<Bson> bsons = new ArrayList<>();
        // 筛选条件
        Bson match = Aggregates.match(Filters.eq("metadata.no", no));

        // 输出对象
        Bson project = Aggregates.project(Projections.fields(
                // 日期格式化
                Projections.computed("date", new Document("$dateToParts", new Document().append("date", "$ts"))),
                Projections.computed("temperature", 1)
        ));

        // 分组统计 平均值
        Bson group = Aggregates.group(new Document("date", new Document().append("year", "$date.year").append("month", "$date.month").append("day", "$date.day")),
                Accumulators.avg("avgTemp", "$temperature")
        );

        bsons.add(match);
        bsons.add(project);
        bsons.add(group);
        log.info(Json.toJson(bsons));
        return zMongoDatabase.getCollection("device").aggregate(bsons);
    }
}
