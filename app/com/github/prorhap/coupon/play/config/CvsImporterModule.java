package com.github.prorhap.coupon.play.config;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import play.Environment;

import java.util.Properties;

public class CvsImporterModule extends AbstractModule {
    private final Environment environment;
    private final Config config;

    public CvsImporterModule(Environment environment, Config config) {
        this.environment = environment;
        this.config = config;
    }

    @Override
    public void configure() {
        bindConstant().annotatedWith(Names.named("couponCreationTopic")).to(config.getString("kafka.couponCreationTopic"));
        bind(KafkaProducer.class).annotatedWith(Names.named("batchProducer")).toInstance(createBatchKafkaProducer());

    }

    private KafkaProducer createBatchKafkaProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "coupon-kafka-client");
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString("kafka.servers"));
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 100000);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1000);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        return new KafkaProducer<>(properties);
    }
}
