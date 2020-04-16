package com.github.prorhap.coupon.play.service;

import com.github.prorhap.coupon.play.common.CouponDateUtils;
import com.github.prorhap.coupon.play.helper.EsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class DefaultCouponNotificationService implements CouponNotificationService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EsClient esClient;
    private final CouponDateUtils couponDateUtils;

    @Inject
    public DefaultCouponNotificationService(EsClient esClient, CouponDateUtils couponDateUtils) {
        this.esClient = esClient;
        this.couponDateUtils = couponDateUtils;
    }

    @Override
    public void sendNotification(int expiryDaysBefore) {
        logger.trace("Check and send a expiry notification");
        esClient.getCouponByExpireDateAndUnusedAndIssuedAsync(couponDateUtils.after(expiryDaysBefore))
                .thenAccept(coupons ->
                        coupons.stream()
                                .forEach(coupon -> logger.info("## SMS to {} for {}: 쿠폰이 3일 후 만료됩니다.", coupon.getUserId(), coupon.getCouponCode())));
    }
}
