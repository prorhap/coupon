package com.github.prorhap.coupon.play.service;

import com.google.inject.ImplementedBy;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.CompletableFuture;

@ImplementedBy(DefaultCouponKafkaProducer.class)
public interface CouponKafkaProducer {
    CompletableFuture<RecordMetadata> send(String key, String value);
}
