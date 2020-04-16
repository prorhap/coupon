package com.github.prorhap.coupon.play.app.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.prorhap.coupon.play.common.CouponDateFormatConstant;
import com.github.prorhap.coupon.play.common.CouponDateUtils;
import com.github.prorhap.coupon.play.common.DynamoAttributeUtils;
import com.github.prorhap.coupon.play.helper.EsClient;
import com.github.prorhap.coupon.play.model.Coupon;
import com.github.prorhap.coupon.play.helper.CouponItemMapper;
import com.github.prorhap.coupon.play.helper.CouponRepository;
import com.github.prorhap.coupon.play.helper.RemoteCache;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class CouponCancelConsumingProcessor implements ConsumingProcessor, CouponDateFormatConstant {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ObjectMapper objectMapper;
    private final CouponRepository couponRepository;
    private final RemoteCache remoteCache;
    private final ExecutorService remoteCacheAsyncExecutor;
    private final DynamoAttributeUtils attributeUtils;
    private final CouponDateUtils couponDateUtils;
    private final EsClient esClient;


    @Inject
    public CouponCancelConsumingProcessor(ObjectMapper objectMapper, CouponRepository couponRepository, RemoteCache remoteCache, @Named("remoteCacheAsyncExecutor") ExecutorService remoteCacheAsyncExecutor, DynamoAttributeUtils attributeUtils, CouponDateUtils couponDateUtils, EsClient esClient) {
        this.objectMapper = objectMapper;
        this.couponRepository = couponRepository;
        this.remoteCache = remoteCache;
        this.remoteCacheAsyncExecutor = remoteCacheAsyncExecutor;
        this.attributeUtils = attributeUtils;
        this.couponDateUtils = couponDateUtils;
        this.esClient = esClient;
    }

    @Override
    public void process(String value) {
        try {
            logger.info("Got a coupon cancel event");
            Coupon polledEvent = objectMapper.readValue(value, Coupon.class);
            Coupon coupon = couponRepository.getItem(polledEvent.getCouponCode(), new CouponItemMapper(attributeUtils));

            Date now = couponDateUtils.now();
            coupon.setUserId("");
            coupon.setIssued(false);
            coupon.setIssuedAt(null);
            coupon.setModifiedAt(now);

            Map<String, AttributeValueUpdate> updateMap =
                    ImmutableMap.of(
                            "userId", attributeUtils.createUpdate(attributeUtils.create(coupon.getUserId())),
                            "issued", attributeUtils.createUpdate(attributeUtils.create(coupon.isIssued())),
                            "issuedAt", attributeUtils.createUpdate(attributeUtils.create(coupon.getIssuedAt(), DATE_TIME_FORMAT)),
                            "modifiedAt", attributeUtils.createUpdate(attributeUtils.create(coupon.getModifiedAt(), DATE_TIME_FORMAT)));

            couponRepository.updateItem(ImmutableMap.of("couponCode", attributeUtils.create(coupon.getCouponCode())), updateMap);
            remoteCache.set(coupon.getCouponCode(), objectMapper.writeValueAsString(coupon));
            esClient.index(coupon);
        }catch (Exception e) {
            logger.error("Error", e);
        }
    }
}
