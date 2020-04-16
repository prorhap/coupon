package com.github.prorhap.coupon.play.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.prorhap.coupon.play.model.Coupon;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class DefaultKafkaProducingService implements KafkaProducingService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CouponKafkaProducer couponCreateProducer;
    private final CouponKafkaProducer couponIssueProducer;
    private final CouponKafkaProducer couponUseProducer;
    private final CouponKafkaProducer couponCancelProducer;
    private final ObjectMapper objectMapper;

    @Inject
    public DefaultKafkaProducingService(@Named("couponCreateProducer") CouponKafkaProducer couponCreateProducer,
                                        @Named("couponIssueProducer") CouponKafkaProducer couponIssueProducer,
                                        @Named("couponUseProducer") CouponKafkaProducer couponUseProducer,
                                        @Named("couponCancelProducer") CouponKafkaProducer couponCancelProducer,
                                        ObjectMapper objectMapper) {

        this.couponCreateProducer = couponCreateProducer;
        this.couponIssueProducer = couponIssueProducer;
        this.couponUseProducer = couponUseProducer;
        this.couponCancelProducer = couponCancelProducer;
        this.objectMapper = objectMapper;
    }

    @Override
    public CompletionStage<Integer> sendCreation(List<Coupon> coupons) {
        try {
            coupons.stream().forEach(coupon -> {
                try {
                    logger.debug("write coupon = {}", coupon.getCouponCode());
                    couponCreateProducer.send(coupon.getCouponCode(), objectMapper.writeValueAsString(coupon))
                            .thenAccept(recordMetadata -> logger.debug("sent coupon creation = {}", recordMetadata));
                } catch (JsonProcessingException e) {
                    logger.error("Error while writing coupon creation");
                    throw new RuntimeException(e);
                }
            });
            return CompletableFuture.completedFuture(coupons.size());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletionStage<Integer> sendIssue(Coupon coupon) {
        try {
            return couponIssueProducer.send(coupon.getCouponCode(), objectMapper.writeValueAsString(coupon))
                    .thenApply(recordMetadata -> recordMetadata.serializedValueSize());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletionStage<Integer> sendUse(Coupon coupon) {
        try {
            return couponUseProducer.send(coupon.getCouponCode(), objectMapper.writeValueAsString(coupon))
                    .thenApply(recordMetadata -> recordMetadata.serializedValueSize());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletionStage<Integer> sendCancel(Coupon coupon) {
        try {
            return couponCancelProducer.send(coupon.getCouponCode(), objectMapper.writeValueAsString(coupon))
                    .thenApply(recordMetadata -> recordMetadata.serializedValueSize());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
