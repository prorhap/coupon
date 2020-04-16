package com.github.prorhap.coupon.play.service;

import com.google.inject.ImplementedBy;

@ImplementedBy(DefaultCouponNotificationService.class)
public interface CouponNotificationService {

    void sendNotification(int expiryDaysBefore);
}
