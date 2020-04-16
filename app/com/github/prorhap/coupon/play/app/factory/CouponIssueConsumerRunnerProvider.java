package com.github.prorhap.coupon.play.app.factory;

import com.github.prorhap.coupon.play.app.ConsumerRunner;
import com.github.prorhap.coupon.play.app.processor.ConsumingProcessor;
import com.github.prorhap.coupon.play.app.DefaultConsumerRunner;
import com.google.inject.Provider;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import javax.inject.Inject;
import javax.inject.Named;

public class CouponIssueConsumerRunnerProvider implements Provider<ConsumerRunner> {

    private final KafkaConsumer consumer;
    private final String topic;
    private final ConsumingProcessor consumingProcessor;

    @Inject
    public CouponIssueConsumerRunnerProvider(@Named("couponIssueConsumer") KafkaConsumer consumer,
                                             @Named("couponIssueTopic") String topic,
                                             @Named("couponIssueConsumingProcessor") ConsumingProcessor consumingProcessor) {
        this.consumer = consumer;
        this.topic = topic;
        this.consumingProcessor = consumingProcessor;
    }

    @Override
    public ConsumerRunner get() {
        return new DefaultConsumerRunner(consumer, topic, consumingProcessor);
    }
}
