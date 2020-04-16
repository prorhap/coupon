package com.github.prorhap.coupon.play.helper;

import com.github.prorhap.coupon.play.model.Coupon;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class CouponRepository extends AbstractDynamoDbRepository<Coupon, String> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    public CouponRepository(@Named("dynamoDbAsyncClient") DynamoDbAsyncClient ddbAsyncClient,
                            @Named("dynamoDbClient") DynamoDbClient ddbClient,
                            @Named("dynamoTableName") String tableName) {
        super(ddbAsyncClient, ddbClient, tableName);
    }

    @Override
    public CompletionStage<Coupon> getItemAsync(String couponCode, ItemMapper<Coupon> itemMapper) {

        logger.info("Get item for {}", couponCode);

        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(tableName)
                .key(ImmutableMap.of("couponCode", AttributeValue.builder().s(couponCode).build()))
                .build();

        return ddbAsyncClient.getItem(getItemRequest)
                .thenApply(getItemResponse -> itemMapper.convert(getItemResponse.item()));
    }

    @Override
    public Coupon getItem(String couponCode, ItemMapper<Coupon> itemMapper) {

        logger.info("Get item for {}", couponCode);

        GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(tableName)
                .key(ImmutableMap.of("couponCode", AttributeValue.builder().s(couponCode).build()))
                .build();

        return itemMapper.convert(ddbClient.getItem(getItemRequest).item());
    }

    @Override
    public void putItem(Coupon coupon, ItemMapper<Coupon> itemMapper) {

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemMapper.convert(coupon))
                .build();

        logger.info("Put into ddb for {}", coupon.getCouponCode());
        PutItemResponse response = ddbClient.putItem(request);
        logger.debug("ddb put result = {} for {}", response.toString(), coupon.getCouponCode());
    }

    @Override
    public void updateItem(Map<String, AttributeValue> itemKey, Map<String, AttributeValueUpdate> updatedValues) {

        logger.info("Updating {} by {}", itemKey, updatedValues);

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(itemKey)
                .attributeUpdates(updatedValues)
                .build();

        UpdateItemResponse response = ddbClient.updateItem(request);
        logger.debug("ddb update result = {} for {}", response.toString());
    }
}
