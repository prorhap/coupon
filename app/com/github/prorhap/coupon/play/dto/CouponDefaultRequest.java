package com.github.prorhap.coupon.play.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CouponDefaultRequest {

    private String couponCode;

    public CouponDefaultRequest(String couponCode) {
        this.couponCode = couponCode;
    }
}
