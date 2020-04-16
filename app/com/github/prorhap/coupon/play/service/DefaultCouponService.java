package com.github.prorhap.coupon.play.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.prorhap.coupon.play.dto.CouponDefaultRequest;
import com.github.prorhap.coupon.play.common.CouponDateUtils;
import com.github.prorhap.coupon.play.common.CouponNotExistException;
import com.github.prorhap.coupon.play.common.DynamoAttributeUtils;
import com.github.prorhap.coupon.play.dto.*;
import com.github.prorhap.coupon.play.helper.*;
import com.github.prorhap.coupon.play.model.*;
import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DefaultCouponService implements CouponService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final int COUPON_CODE_LENGTH = 19;

    private final CouponCodeGenerator couponCodeGenerator;
    private final KafkaProducingService kafkaProducingService;
    private final RemoteCache remoteCache;
    private final CouponRepository couponRepository;
    private final CouponFactory couponFactory;
    private final CouponDateUtils couponDateUtils;
    private final EsClient esClient;

    private final ObjectMapper objectMapper;

    @Inject
    public DefaultCouponService(CouponCodeGenerator couponCodeGenerator,
                                KafkaProducingService kafkaProducingService,
                                RemoteCache remoteCache,
                                CouponRepository couponRepository, CouponFactory couponFactory,
                                CouponDateUtils couponDateUtils,
                                EsClient esClient, ObjectMapper objectMapper) {
        this.couponCodeGenerator = couponCodeGenerator;
        this.kafkaProducingService = kafkaProducingService;
        this.remoteCache = remoteCache;
        this.couponRepository = couponRepository;
        this.couponFactory = couponFactory;
        this.couponDateUtils = couponDateUtils;
        this.esClient = esClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public CompletionStage<Integer> createCoupon(CreateCouponRequest createCouponRequest) {
        return kafkaProducingService
                .sendCreation(
                        IntStream.range(0, createCouponRequest.getAmount())
                                .mapToObj(i -> couponCodeGenerator.generate(COUPON_CODE_LENGTH))
                                .map(code -> couponFactory.create(
                                        code,
                                        createCouponRequest.getValidFrom(),
                                        createCouponRequest.getExpireAt()))
                                .collect(Collectors.toList()))
                .thenApply(result -> {
                    logger.debug("return coupon creation");
                    return createCouponRequest.getAmount();
                });
    }

    @Override
    public CompletionStage<CouponIssueValidationResult> issueCoupon(CouponIssueRequest couponIssueRequest) {
        return getCoupon(couponIssueRequest.getCouponCode())
                .thenCompose(coupon -> {
                    logger.info("read coupon to issue = {}", coupon);
                    CouponIssueValidationResult issuePutResult = coupon.isIssueable(couponDateUtils.now());
                    logger.debug("coupon issue validation result = {}", issuePutResult);
                    if (issuePutResult == CouponIssueValidationResult.OK) {
                        coupon.setUserId(couponIssueRequest.getUserId());
                        return kafkaProducingService.sendIssue(coupon)
                                .thenApply(sendCount -> issuePutResult);
                    }
                    return CompletableFuture.supplyAsync(() -> issuePutResult);
                });
    }

    @Override
    public CompletionStage<CouponUseValidationResult> useCoupon(CouponDefaultRequest couponUseRequest) {
        return getCoupon(couponUseRequest.getCouponCode())
                .thenCompose(coupon -> {
                    logger.info("read coupon to use = {}", coupon);
                    CouponUseValidationResult usePutResult = coupon.isUseable(couponDateUtils.now());
                    logger.debug("coupon use validation result = {}", usePutResult);
                    if (usePutResult == CouponUseValidationResult.OK) {
                        return kafkaProducingService.sendUse(coupon)
                                .thenApply(sendCount -> usePutResult);
                    }
                    return CompletableFuture.completedFuture(usePutResult);
                });
    }

    @Override
    public CompletionStage<CouponCancelValidationResult> cancelCoupon(CouponDefaultRequest couponUseRequest) {
        return getCoupon(couponUseRequest.getCouponCode())
                .thenCompose(coupon -> {
                    logger.info("read coupon to use = {}", coupon);
                    CouponCancelValidationResult usePutResult = coupon.isCancelable(couponDateUtils.now());
                    logger.debug("coupon cancel validation result = {}", usePutResult);
                    if (usePutResult == CouponCancelValidationResult.OK) {
                        return kafkaProducingService.sendCancel(coupon)
                                .thenApply(sendCount -> usePutResult);
                    }
                    return CompletableFuture.completedFuture(usePutResult);
                });
    }

    @Override
    public CompletionStage<List<CouponResponse>> getTodayExpiredCoupons() {

        return esClient.getCouponByExpireDateAndUnusedAsync(couponDateUtils.today())
                .thenApply(coupons -> coupons.stream().map(coupon -> CouponResponse.from(coupon))
                        .collect(Collectors.toList()));
    }

    private CompletionStage<Coupon> getCoupon(String couponCode) {
        return remoteCache.get(couponCode)
                .thenCompose(couponJson -> {
                    try {
                        if (StringUtils.isEmpty(couponJson)) {
                            return couponRepository.getItemAsync(couponCode, new CouponItemMapper(new DynamoAttributeUtils()))
                                    .thenApply(coupon -> {
                                        if (Objects.isNull(coupon)) {
                                            throw new CouponNotExistException("The coupon doesn't exist. couponCode = " + couponCode);
                                        }
                                        remoteCache.set(coupon.getCouponCode(), Json.toJson(coupon).toString());
                                        return coupon;
                                    });
                        }
                        Coupon coupon = objectMapper.readValue(couponJson, Coupon.class);
                        return CompletableFuture.supplyAsync(() -> coupon);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
