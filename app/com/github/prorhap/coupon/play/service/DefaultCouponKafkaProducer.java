package com.github.prorhap.coupon.play.service;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DefaultCouponKafkaProducer implements CouponKafkaProducer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String topic;
    private final KafkaProducer producer;

    @Inject
    public DefaultCouponKafkaProducer(String topic, KafkaProducer producer) {
        this.topic = topic;
        this.producer = producer;
    }

    @Override
    public CompletableFuture<RecordMetadata> send(String key, String value) {
        CompletableFuture<RecordMetadata> completableFuture = new CompletableFuture<>();

        Callback callback = (metadata, exception) -> {
            if (Objects.isNull(exception)) {
                logger.debug("[{}] Complete to write = {}", topic, metadata);
                completableFuture.complete(metadata);
            } else {
                logger.error("Failed to write message.");
                completableFuture.completeExceptionally(exception);
            }
        };

        producer.send(new ProducerRecord(topic, key, value), callback);

        return completableFuture;
    }
}
