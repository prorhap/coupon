package com.github.prorhap.coupon.play.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.prorhap.coupon.play.common.CouponCodeSerializer;
import com.github.prorhap.coupon.play.model.Coupon;

public class CouponResponse {

    @JsonSerialize(using = CouponCodeSerializer.class)
    private String couponCode;

    public CouponResponse(String couponCode) {
        this.couponCode = couponCode;
    }

    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(coupon.getCouponCode());
    }
}
