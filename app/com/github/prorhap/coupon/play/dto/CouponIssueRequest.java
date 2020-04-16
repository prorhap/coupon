package com.github.prorhap.coupon.play.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CouponIssueRequest {

    private String userId;
    private String couponCode;

    public CouponIssueRequest(String userId, String couponCode) {
        this.userId = userId;
        this.couponCode = couponCode;
    }
}
