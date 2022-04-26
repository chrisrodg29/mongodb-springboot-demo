package com.example.mongodb_spring_boot_demo.spring_data_example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
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
                                       MongoDatabase database) {
        return new MongoTemplate(mongoClient, database.getName());
    }

}
