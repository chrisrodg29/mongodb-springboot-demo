package com.example.mongodb_spring_boot_demo.healthcheck;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(value = "databaseConfiguration.mongodb2.uri")
public class HealthCheckDao {

    public static final Document HEALTH_CHECK_COMMAND = new Document("dbStats", 1);

    private final MongoDatabase database2;

    public HealthCheckDao(MongoDatabase database2) {
        this.database2 = database2;
    }

    public Document performHealthCheck() {
        return database2.runCommand(HEALTH_CHECK_COMMAND);
    }
}
