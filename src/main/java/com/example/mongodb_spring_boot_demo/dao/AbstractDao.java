package com.example.mongodb_spring_boot_demo.dao;

import com.example.mongodb_spring_boot_demo.config.data.MongoDBConfiguration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.context.annotation.Configuration;

@Configuration
public abstract class AbstractDao {

    protected MongoDatabase db;
    protected MongoClient client;
    protected MongoDBConfiguration dbConfiguration;

    protected AbstractDao(MongoClient client, MongoDBConfiguration dbConfiguration) {
        this.client = client;
        this.dbConfiguration = dbConfiguration;
        this.db = this.client.getDatabase(dbConfiguration.getDatabaseName());
    }

}
