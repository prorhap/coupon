package com.github.prorhap.coupon.play.helper;

import com.github.prorhap.coupon.play.common.DynamoAttributeUtils;
import com.github.prorhap.coupon.play.model.Coupon;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class CouponItemMapper implements ItemMapper<Coupon> {

    private final DynamoAttributeUtils attributeUtils;

    @Inject
    public CouponItemMapper(DynamoAttributeUtils attributeUtils) {
        this.attributeUtils = attributeUtils;
    }

    @Override
    public Coupon convert(Map<String, AttributeValue> item) {
        return new Coupon(
                attributeUtils.getString(item, "couponCode"),
                attributeUtils.getBoolean(item, "used"),
                attributeUtils.getBoolean(item, "issued"),
                attributeUtils.getDate(item, "validFrom", DATE_FORMAT),
                attributeUtils.getDate(item, "expireAt", DATE_FORMAT),
                attributeUtils.getString(item, "userId"),
                attributeUtils.getDate(item, "issuedAt", DATE_TIME_FORMAT),
                attributeUtils.getDate(item, "usedAt", DATE_TIME_FORMAT),
                attributeUtils.getDate(item, "createdAt", DATE_TIME_FORMAT),
                attributeUtils.getDate(item, "modifiedAt", DATE_TIME_FORMAT));
    }

    @Override
    public Map<String, AttributeValue> convert(Coupon coupon) {

        HashMap<String,AttributeValue> itemValues = new HashMap<String,AttributeValue>();

        itemValues.put("couponCode", attributeUtils.create(coupon.getCouponCode()));
        itemValues.put("used", attributeUtils.create(coupon.isUsed()));
        itemValues.put("issued", attributeUtils.create(coupon.isIssued()));
        itemValues.put("validFrom", attributeUtils.create(coupon.getValidFrom(), DATE_FORMAT));
        itemValues.put("expireAt", attributeUtils.create(coupon.getExpireAt(), DATE_FORMAT));
        itemValues.put("userId", attributeUtils.create(coupon.getUserId()));
        itemValues.put("issuedAt", attributeUtils.create(coupon.getIssuedAt(), DATE_TIME_FORMAT));
        itemValues.put("usedAt", attributeUtils.create(coupon.getUsedAt(), DATE_TIME_FORMAT));
        itemValues.put("createdAt", attributeUtils.create(coupon.getCreatedAt(), DATE_TIME_FORMAT));
        itemValues.put("modifiedAt", attributeUtils.create(coupon.getModifiedAt(), DATE_TIME_FORMAT));

        return itemValues;
    }
}
