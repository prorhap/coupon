package com.github.prorhap.coupon.play.helper;

import com.github.prorhap.coupon.play.model.Coupon;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public abstract class AbstractDynamoDbRepository<T, PrimaryKey extends Serializable> {

    protected final DynamoDbAsyncClient ddbAsyncClient;
    protected DynamoDbClient ddbClient;
    protected final String tableName;

    protected AbstractDynamoDbRepository(DynamoDbAsyncClient ddbAsyncClient, DynamoDbClient ddbClient, String tableName) {
        this.ddbAsyncClient = ddbAsyncClient;
        this.ddbClient = ddbClient;
        this.tableName = tableName;
    }

    public abstract CompletionStage<T> getItemAsync(PrimaryKey primaryKey, ItemMapper<Coupon> itemMapper);
    public abstract T getItem(PrimaryKey primaryKey, ItemMapper<Coupon> itemMapper);
    public abstract void putItem(T coupon, ItemMapper<T> itemMapper);
    public abstract void updateItem(Map<String, AttributeValue> itemKey, Map<String, AttributeValueUpdate> updatedValues);

}
