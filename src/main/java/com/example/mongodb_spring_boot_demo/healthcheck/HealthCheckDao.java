package com.example.mongodb_spring_boot_demo.healthcheck;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(value = "spring.mongodb2.uri")
public class HealthCheckDao {

    private final MongoDatabase database2;

    public HealthCheckDao(MongoDatabase database2) {
        this.database2 = database2;
    }

    public void performHealthCheck() {
        database2.runCommand(new Document("dbStats", 1));
    }
}
