package com.github.prorhap.coupon.play.api.controller;

import com.github.prorhap.coupon.play.dto.CouponDefaultRequest;
import com.github.prorhap.coupon.play.dto.CouponIssueRequest;
import com.github.prorhap.coupon.play.service.CouponService;
import com.github.prorhap.coupon.play.dto.CreateCouponRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class CouponController extends Controller {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CouponService couponService;

    @Inject
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    public CompletionStage<Result> create(Http.Request request) {
        Optional<CreateCouponRequest> createCouponRequest = request.body().parseJson(CreateCouponRequest.class);
        logger.info("-c-> create with {}", request.body().asText());

        return couponService.createCoupon(createCouponRequest.orElseGet(() -> CreateCouponRequest.create()))
                .thenApplyAsync(response -> ok(Json.toJson(response)));
    }

    public CompletionStage<Result> issue(Http.Request request) {
        Optional<CouponIssueRequest> couponIssueRequest = request.body().parseJson(CouponIssueRequest.class);
        return couponService.issueCoupon(couponIssueRequest.get())
                .thenApplyAsync(response -> ok(Json.toJson(response)));
    }

    public CompletionStage<Result> use(Http.Request request) {
        Optional<CouponDefaultRequest> couponDefaultRequest = request.body().parseJson(CouponDefaultRequest.class);
        return couponService.useCoupon(couponDefaultRequest.get())
                .thenApplyAsync(response -> ok(Json.toJson(response)));
    }

    public CompletionStage<Result> cancel(Http.Request request) {
        Optional<CouponDefaultRequest> couponDefaultRequest = request.body().parseJson(CouponDefaultRequest.class);
        return couponService.cancelCoupon(couponDefaultRequest.get())
                .thenApplyAsync(response -> ok(Json.toJson(response)));
    }

    public CompletionStage<Result> getCouponsExpiredToday() {
        return couponService.getTodayExpiredCoupons()
                .thenApplyAsync(response -> ok(Json.toJson(response)));

    }
}
