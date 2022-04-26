package com.example.mongodb_spring_boot_demo.healthcheck;

import com.example.mongodb_spring_boot_demo.secondary_db_config_example.SecondaryMongoDBConfiguration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(value = "spring.mongodb2.uri")
public class HealthCheckDao {

    private final MongoDatabase secondaryDb;

    public HealthCheckDao(MongoClient secondaryMongoClient, SecondaryMongoDBConfiguration secondaryConfiguration) {
        this.secondaryDb = secondaryMongoClient.getDatabase(
                secondaryConfiguration.getDatabaseName()
        );
    }

    public void performHealthCheck() {
        secondaryDb.runCommand(new Document("dbStats", 1));
    }
}
