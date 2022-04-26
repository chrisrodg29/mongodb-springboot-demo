package com.example.mongodb_spring_boot_demo.config.data;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Configuration
public class MongoDBConfiguration {

    @Value("${spring.mongodb.uri}")
    private String mongoUri;
    @Value("${spring.mongodb.database}")
    private String databaseName;

    @Bean
    @Scope(value = ConfigurableListableBeanFactory.SCOPE_SINGLETON)
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(mongoUri);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .writeConcern(WriteConcern.MAJORITY)
                .codecRegistry(getCodecRegistry())
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        return MongoClients.create(settings);
    }

    private CodecRegistry getCodecRegistry() {
        return fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );
    }

    @Bean
    @Scope(value = ConfigurableListableBeanFactory.SCOPE_SINGLETON)
    public MongoDatabase database(MongoClient mongoClient) {
        return mongoClient.getDatabase(databaseName);
    }

}
