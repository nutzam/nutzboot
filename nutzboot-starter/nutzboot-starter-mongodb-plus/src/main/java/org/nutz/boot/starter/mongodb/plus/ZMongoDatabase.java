package org.nutz.boot.starter.mongodb.plus;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import org.bson.Document;
import org.nutz.lang.Lang;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wizzer@qq.com
 */
public class ZMongoDatabase {

    private MongoDatabase db;

    public ZMongoDatabase(MongoDatabase db) {
        this.db = db;
    }

    /**
     * 获取集合，集合不存在则返回 null
     *
     * @param name 集合名称
     * @return 集合薄封装
     */
    public MongoCollection<Document> getCollection(String name) {
        if (!this.collectionExists(name)) {
            return null;
        }
        return db.getCollection(name);
    }

    /**
     * 获取一个集合，如果集合不存在，就创建它
     *
     * @param name         集合名
     * @param dropIfExists true 如果存在就清除
     * @return 集合薄封装
     */
    public MongoCollection<Document> createCollection(String name, boolean dropIfExists) {
        // 不存在则创建
        if (!this.collectionExists(name)) {
            return createCollection(name, null);
        }
        // 固定清除
        else if (dropIfExists) {
            db.getCollection(name).drop();
            return createCollection(name, null);
        }
        // 已经存在
        return db.getCollection(name);
    }

    /**
     * 获取一个集合，如果集合不存在，就创建它
     *
     * @param name         集合名
     * @param options      集合配置信息
     * @param dropIfExists true 如果存在就清除
     * @return 集合薄封装
     */
    public MongoCollection<Document> createCollection(String name, CreateCollectionOptions options, boolean dropIfExists) {
        // 不存在则创建
        if (!this.collectionExists(name)) {
            return createCollection(name, options);
        }
        // 固定清除
        else if (dropIfExists) {
            db.getCollection(name).drop();
            return createCollection(name, options);
        }
        // 已经存在
        return db.getCollection(name);
    }

    /**
     * 创建一个集合
     *
     * @param name    集合名
     * @param options 集合配置信息
     * @return 集合薄封装
     */
    public MongoCollection<Document> createCollection(String name, CreateCollectionOptions options) {
        if (this.collectionExists(name)) {
            throw Lang.makeThrow("Colection has exists: %s.%s", db.getName(), name);
        }
        // 创建默认配置信息
        if (null == options) {
            options = new CreateCollectionOptions().capped(false);
        }
        db.createCollection(name, options);
        return db.getCollection(name);
    }

    /**
     * 判断集合是否存在
     *
     * @param collectionName 集合名
     * @return
     */
    public boolean collectionExists(String collectionName) {
        return listCollectionNames().contains(collectionName);
    }

    /**
     * @return 当前数据库所有可用集合名称
     */
    public List<String> listCollectionNames() {
        return db.listCollectionNames().into(new ArrayList<String>());
    }

    public MongoDatabase getNativeDB() {
        return this.db;
    }
}
