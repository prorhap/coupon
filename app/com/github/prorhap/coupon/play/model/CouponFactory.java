package com.github.prorhap.coupon.play.model;

import com.github.prorhap.coupon.play.common.CouponDateUtils;
import com.github.prorhap.coupon.play.model.Coupon;

import javax.inject.Inject;
import java.util.Date;

public class CouponFactory {

    private final CouponDateUtils couponDateUtils;

    @Inject
    public CouponFactory(CouponDateUtils couponDateUtils) {
        this.couponDateUtils = couponDateUtils;
    }

    public Coupon create(String couponCode, Date validFrom, Date expireAt) {
        Date now = couponDateUtils.now();
        return new Coupon(couponCode, false, false, validFrom, expireAt, null, null, null, now, now);
    }
}
