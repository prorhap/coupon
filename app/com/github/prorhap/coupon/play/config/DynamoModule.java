package com.github.prorhap.coupon.play.config;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

public class DynamoModule extends AbstractModule {
    private final play.Environment environment;
    private final Config config;

    public DynamoModule(play.Environment environment, Config config) {
        this.environment = environment;
        this.config = config;
    }

    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("dynamoTableName")).to(config.getString("dynamodb.table"));

        bind(DynamoDbAsyncClient.class).annotatedWith(Names.named("dynamoDbAsyncClient")).toInstance(getDynamoDbAsyncClient());
        bind(DynamoDbClient.class).annotatedWith(Names.named("dynamoDbClient")).toInstance(getDynamoDbClient());
    }

    private DynamoDbAsyncClient getDynamoDbAsyncClient() {
        return DynamoDbAsyncClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .endpointOverride(URI.create(config.getString("dynamodb.uri")))
                .build();
    }

    private DynamoDbClient getDynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .endpointOverride(URI.create(config.getString("dynamodb.uri")))
                .build();
    }
}
