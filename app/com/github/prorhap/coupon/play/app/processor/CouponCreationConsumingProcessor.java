package com.github.prorhap.coupon.play.app.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.prorhap.coupon.play.common.DynamoAttributeUtils;
import com.github.prorhap.coupon.play.helper.EsClient;
import com.github.prorhap.coupon.play.model.Coupon;
import com.github.prorhap.coupon.play.helper.CouponItemMapper;
import com.github.prorhap.coupon.play.helper.CouponRepository;
import com.github.prorhap.coupon.play.helper.RemoteCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class CouponCreationConsumingProcessor implements ConsumingProcessor {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ObjectMapper objectMapper;
    private final CouponRepository couponRepository;
    private final RemoteCache remoteCache;
    private final EsClient esClient;

    @Inject
    public CouponCreationConsumingProcessor(ObjectMapper objectMapper, CouponRepository couponRepository, RemoteCache remoteCache, EsClient esClient) {
        this.objectMapper = objectMapper;
        this.couponRepository = couponRepository;
        this.remoteCache = remoteCache;
        this.esClient = esClient;
    }

    @Override
    public void process(String value) {
        try {
            Coupon coupon = objectMapper.readValue(value, Coupon.class);
            couponRepository.putItem(coupon, new CouponItemMapper(new DynamoAttributeUtils()));
            remoteCache.set(coupon.getCouponCode(), value);
            esClient.index(coupon);
        } catch (Exception e) {
            logger.error("Error", e);
        }
    }
}
