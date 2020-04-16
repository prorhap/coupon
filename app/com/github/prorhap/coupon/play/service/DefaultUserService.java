package com.github.prorhap.coupon.play.service;

import com.github.prorhap.coupon.play.dto.CouponResponse;
import com.github.prorhap.coupon.play.helper.EsClient;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class DefaultUserService implements UserService {

    private final EsClient esClient;

    @Inject
    public DefaultUserService(EsClient esClient) {
        this.esClient = esClient;
    }

    @Override
    public CompletionStage<List<CouponResponse>> getCoupons(String userId) {
        return esClient.getCouponByUserIdAsync(userId)
                .thenApply(coupons -> coupons.stream().map(coupon -> CouponResponse.from(coupon))
                .collect(Collectors.toList()));
    }
}
