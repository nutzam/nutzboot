package org.nutz.boot.starter.mongodb.plus;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.nutz.lang.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
public class ZMongoClient {

    public static ZMongoClient me(MongoClient mc, String databaseName) {
        return new ZMongoClient(mc, databaseName);
    }

    public static ZMongoClient me(String connectionString) {
        ConnectionString connection = new ConnectionString(connectionString);
        return new ZMongoClient(MongoClients.create(connection), connection.getDatabase());
    }

    public static ZMongoClient me(ConnectionString connection) {
        if (Strings.isNotBlank(connection.getDatabase())) {
            return new ZMongoClient(MongoClients.create(connection), connection.getDatabase());
        } else {
            return new ZMongoClient(MongoClients.create(connection));
        }
    }

    /**
     * 保持一个连接实例
     */
    private MongoClient moclient;

    /**
     * 数据库名称
     */
    private String databaseName;

    private ZMongoClient(MongoClient mc, String databaseName) {
        this.moclient = mc;
        this.databaseName = databaseName;
    }

    private ZMongoClient(MongoClient mc) {
        this.moclient = mc;
    }

    public void close() {
        this.moclient.close();
    }

    public ZMongoDatabase db() {
        if (this.databaseName == null) {
            return null;
        }
        return new ZMongoDatabase(this.moclient.getDatabase(this.databaseName));
    }

    public ZMongoDatabase db(String databaseName) {
        return new ZMongoDatabase(this.moclient.getDatabase(databaseName));
    }

    /**
     * @return 当前服务器的数据库名称列表
     */
    public List<String> listDatabaseNames() {
        return this.moclient.listDatabaseNames().into(new ArrayList<String>());
    }


    /**
     * 获取collection对象 - 指定Collection
     *
     * @param databaseName
     * @param collectionName
     * @return
     */
    public MongoCollection<Document> getMongoCollection(String databaseName, String collectionName) {
        if (Strings.isBlank(databaseName)) {
            return null;
        }
        if (Strings.isBlank(collectionName)) {
            return null;
        }
        return this.moclient.getDatabase(databaseName).getCollection(collectionName);
    }
}
