package io.nutz.demo.simple;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.TimeSeriesGranularity;
import com.mongodb.client.model.TimeSeriesOptions;
import com.mongodb.client.result.InsertManyResult;
import io.nutz.demo.simple.bean.Device;
import org.bson.Document;
import org.nutz.boot.NbApp;
import org.nutz.boot.starter.mongodb.plus.ZMongoDatabase;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
@IocBean(create = "init")
public class MainLauncher {
    private static final Log log = Logs.get();

    @Inject
    private ZMongoDatabase zMongoDatabase;

    public void init() {
        CreateCollectionOptions collectionOptions = new CreateCollectionOptions();
        TimeSeriesOptions timeSeriesOptions = new TimeSeriesOptions("ts");
        timeSeriesOptions.metaField("metadata");
        timeSeriesOptions.granularity(TimeSeriesGranularity.SECONDS);
        collectionOptions.timeSeriesOptions(timeSeriesOptions);
        MongoCollection<Document> deviceCollection = zMongoDatabase.createCollection("device", collectionOptions, true);
        List<Document> list = new ArrayList<>();
        Device device = new Device(Times.now(), 36.7, "0001");
        list.add(new Document().append("ts", device.getTs()).append("temperature", device.getTemperature()).append("metadata",
                new Document().append("no", device.getNo())));

        Device device1 = new Device(Times.now(), 35.2, "0002");
        list.add(new Document().append("ts", device1.getTs()).append("temperature", device1.getTemperature()).append("metadata",
                new Document().append("no", device1.getNo())));

        Device device2 = new Device(Times.now(), 10.7, "0002");
        list.add(new Document().append("ts", device2.getTs()).append("temperature", device2.getTemperature()).append("metadata",
                new Document().append("no", device2.getNo())));

        Device device3 = new Device(Times.nextDay(Times.now(), 1), 30.0, "0002");
        list.add(new Document().append("ts", device3.getTs()).append("temperature", device3.getTemperature()).append("metadata",
                new Document().append("no", device3.getNo())));

        // Document.parse(Json.toJson(new Device()));

        InsertManyResult insertManyResult = deviceCollection.insertMany(list);
        log.info(Json.toJson(insertManyResult));
    }

    public static void main(String[] args) throws Exception {
        new NbApp().setPrintProcDoc(true).run();
    }
}
