package com.github.prorhap.coupon.play.service;

import com.github.prorhap.coupon.play.model.Coupon;
import com.google.inject.ImplementedBy;

import java.util.List;
import java.util.concurrent.CompletionStage;

@ImplementedBy(DefaultKafkaProducingService.class)
public interface KafkaProducingService {
    CompletionStage<Integer> sendCreation(List<Coupon> coupon);
    CompletionStage<Integer> sendIssue(Coupon coupon);
    CompletionStage<Integer> sendUse(Coupon coupon);
    CompletionStage<Integer> sendCancel(Coupon coupon);
}
