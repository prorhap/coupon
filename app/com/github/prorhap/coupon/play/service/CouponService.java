package com.github.prorhap.coupon.play.service;

import com.github.prorhap.coupon.play.dto.CouponDefaultRequest;
import com.github.prorhap.coupon.play.dto.*;
import com.github.prorhap.coupon.play.model.CouponCancelValidationResult;
import com.github.prorhap.coupon.play.model.CouponIssueValidationResult;
import com.github.prorhap.coupon.play.model.CouponUseValidationResult;
import com.google.inject.ImplementedBy;

import java.util.List;
import java.util.concurrent.CompletionStage;

@ImplementedBy(DefaultCouponService.class)
public interface CouponService {
    CompletionStage<Integer> createCoupon(CreateCouponRequest createCouponRequest);

    CompletionStage<CouponIssueValidationResult> issueCoupon(CouponIssueRequest couponIssueRequest);

    CompletionStage<CouponUseValidationResult> useCoupon(CouponDefaultRequest couponUseRequest);

    CompletionStage<CouponCancelValidationResult> cancelCoupon(CouponDefaultRequest couponDefaultRequest);

    CompletionStage<List<CouponResponse>> getTodayExpiredCoupons();
}
