package com.github.prorhap.coupon.play.config;

import com.github.prorhap.coupon.play.app.ConsumerRunner;
import com.github.prorhap.coupon.play.app.factory.CouponCancelConsumerRunnerProvider;
import com.github.prorhap.coupon.play.app.factory.CouponCreationConsumerRunnerProvider;
import com.github.prorhap.coupon.play.app.factory.CouponIssueConsumerRunnerProvider;
import com.github.prorhap.coupon.play.app.factory.CouponUseConsumerRunnerProvider;
import com.github.prorhap.coupon.play.app.processor.*;
import com.github.prorhap.coupon.play.service.CouponKafkaProducer;
import com.github.prorhap.coupon.play.service.DefaultCouponKafkaProducer;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import play.Environment;

import java.util.Properties;

public class KafkaModule extends AbstractModule {

    private final Environment environment;
    private final Config config;

    public KafkaModule(Environment environment, Config config) {
        this.environment = environment;
        this.config = config;
    }

    @Override
    protected void configure() {

        bindConstant().annotatedWith(Names.named("couponCreationTopic")).to(config.getString("kafka.couponCreationTopic"));
        bindConstant().annotatedWith(Names.named("couponIssueTopic")).to(config.getString("kafka.couponIssueTopic"));
        bindConstant().annotatedWith(Names.named("couponUseTopic")).to(config.getString("kafka.couponUseTopic"));
        bindConstant().annotatedWith(Names.named("couponCancelTopic")).to(config.getString("kafka.couponCancelTopic"));

        bind(CouponKafkaProducer.class)
                .annotatedWith(Names.named("couponCreateProducer"))
                .toInstance(new DefaultCouponKafkaProducer(config.getString("kafka.couponCreationTopic"), createCouponKafkaProducer()));

        bind(CouponKafkaProducer.class)
                .annotatedWith(Names.named("couponIssueProducer"))
                .toInstance(new DefaultCouponKafkaProducer(config.getString("kafka.couponIssueTopic"), createCouponKafkaProducer()));

        bind(CouponKafkaProducer.class)
                .annotatedWith(Names.named("couponUseProducer"))
                .toInstance(new DefaultCouponKafkaProducer(config.getString("kafka.couponUseTopic"), createCouponKafkaProducer()));

        bind(CouponKafkaProducer.class)
                .annotatedWith(Names.named("couponCancelProducer"))
                .toInstance(new DefaultCouponKafkaProducer(config.getString("kafka.couponCancelTopic"), createCouponKafkaProducer()));

        bind(KafkaConsumer.class).annotatedWith(Names.named("couponCreationConsumer")).toInstance(createKafkaConsumer("group-10"));
        bind(KafkaConsumer.class).annotatedWith(Names.named("couponIssueConsumer")).toInstance(createKafkaConsumer("group-1"));
        bind(KafkaConsumer.class).annotatedWith(Names.named("couponUseConsumer")).toInstance(createKafkaConsumer("group-1"));
        bind(KafkaConsumer.class).annotatedWith(Names.named("couponCancelConsumer")).toInstance(createKafkaConsumer("group-1"));

        bind(ConsumingProcessor.class).annotatedWith(Names.named("couponCreationConsumingProcessor")).to(CouponCreationConsumingProcessor.class);
        bind(ConsumingProcessor.class).annotatedWith(Names.named("couponIssueConsumingProcessor")).to(CouponIssueConsumingProcessor.class);
        bind(ConsumingProcessor.class).annotatedWith(Names.named("couponUseConsumingProcessor")).to(CouponUseConsumingProcessor.class);
        bind(ConsumingProcessor.class).annotatedWith(Names.named("couponCancelConsumingProcessor")).to(CouponCancelConsumingProcessor.class);

        bind(ConsumerRunner.class)
                .annotatedWith(Names.named("couponCreationConsumerRunner"))
                .toProvider(CouponCreationConsumerRunnerProvider.class);

        bind(ConsumerRunner.class)
                .annotatedWith(Names.named("couponIssueConsumerRunner"))
                .toProvider(CouponIssueConsumerRunnerProvider.class);

        bind(ConsumerRunner.class)
                .annotatedWith(Names.named("couponUseConsumerRunner"))
                .toProvider(CouponUseConsumerRunnerProvider.class);

        bind(ConsumerRunner.class)
                .annotatedWith(Names.named("couponCancelConsumerRunner"))
                .toProvider(CouponCancelConsumerRunnerProvider.class);
    }

    private KafkaProducer createCouponKafkaProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "coupon-kafka-client");
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString("kafka.servers"));
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        return new KafkaProducer<>(properties);
    }


    private KafkaConsumer createKafkaConsumer(String groupId) {
        Properties config = new Properties();
        config.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.setProperty("enable.auto.commit", "true");
        config.setProperty("auto.commit.interval.ms", "1000");
        config.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        config.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        return new KafkaConsumer<>(config);
    }
}
