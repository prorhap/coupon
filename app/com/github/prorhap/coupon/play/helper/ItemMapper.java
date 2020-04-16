package com.github.prorhap.coupon.play.helper;

import com.github.prorhap.coupon.play.common.CouponDateFormatConstant;
import com.github.prorhap.coupon.play.model.Coupon;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public interface ItemMapper<T> extends CouponDateFormatConstant {
    T convert(Map<String, AttributeValue> item);

    Map<String, AttributeValue> convert(Coupon coupon);
}
