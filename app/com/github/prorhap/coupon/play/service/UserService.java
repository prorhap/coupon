package com.github.prorhap.coupon.play.service;

import com.github.prorhap.coupon.play.dto.CouponResponse;
import com.google.inject.ImplementedBy;

import java.util.List;
import java.util.concurrent.CompletionStage;

@ImplementedBy(DefaultUserService.class)
public interface UserService {
    CompletionStage<List<CouponResponse>> getCoupons(String userId);
}
