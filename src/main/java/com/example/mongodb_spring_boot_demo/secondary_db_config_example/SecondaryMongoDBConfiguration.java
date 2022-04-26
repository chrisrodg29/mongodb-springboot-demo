package com.example.mongodb_spring_boot_demo.secondary_db_config_example;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Configuration
@ConditionalOnProperty(value = "spring.mongodb2.uri")
public class SecondaryMongoDBConfiguration {

    @Value("${spring.mongodb2.uri}")
    private String mongoUri;
    @Value("${spring.mongodb2.database}")
    private String databaseName;

    @Bean
    @Scope(value = ConfigurableListableBeanFactory.SCOPE_SINGLETON)
    public MongoClient mongoClient2() {
        ConnectionString connectionString = new ConnectionString(mongoUri);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applyToSocketSettings(builder -> {
                    builder.connectTimeout(2000, MILLISECONDS);
                })
                .applyToClusterSettings(builder -> {
                    builder.serverSelectionTimeout(1, MILLISECONDS);
                })
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
    public MongoDatabase database2(MongoClient mongoClient2) {
        return mongoClient2.getDatabase(databaseName);
    }

}
