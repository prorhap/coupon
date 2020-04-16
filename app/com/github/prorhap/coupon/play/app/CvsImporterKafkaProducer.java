package com.github.prorhap.coupon.play.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.prorhap.coupon.play.model.Coupon;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

public class CvsImporterKafkaProducer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String topic;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Inject
    public CvsImporterKafkaProducer(@Named("couponCreationTopic") String topic, @Named("batchProducer") KafkaProducer kafkaProducer, ObjectMapper objectMapper) {
        this.topic = topic;
        this.kafkaProducer = kafkaProducer;
        this.objectMapper = objectMapper;
    }

    public void sendCreation(Coupon coupon) throws JsonProcessingException {
        kafkaProducer.send(new ProducerRecord(topic, coupon.getCouponCode(), objectMapper.writeValueAsString(coupon)));
    }
}
