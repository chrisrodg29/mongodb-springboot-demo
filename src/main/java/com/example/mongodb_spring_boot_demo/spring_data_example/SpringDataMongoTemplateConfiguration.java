package com.example.mongodb_spring_boot_demo.spring_data_example;

import com.example.mongodb_spring_boot_demo.config.data.MongoDBConfiguration;
import com.mongodb.client.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "com.example.mongodb_spring_boot_demo.spring_data_example.repository"
)
public class SpringDataMongoTemplateConfiguration {

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient,
                                       MongoDBConfiguration mongoDBConfiguration) {
        return new MongoTemplate(mongoClient, mongoDBConfiguration.getDatabaseName());
    }

}
