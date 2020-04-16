package com.github.prorhap.coupon.play.app;

import com.github.prorhap.coupon.play.app.processor.ConsumingProcessor;
import com.google.inject.assistedinject.Assisted;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Duration;
import java.util.Collections;

public class DefaultConsumerRunner implements ConsumerRunner {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Consumer consumer;
    private final String topic;
    private final ConsumingProcessor consumingProcessor;

    @Inject
    public DefaultConsumerRunner(Consumer consumer, @Assisted String topic, @Assisted ConsumingProcessor consumingProcessor) {
        this.consumer = consumer;
        this.topic = topic;
        this.consumingProcessor = consumingProcessor;
    }

    @Override
    public void run() {
        try {
            logger.info("[{}] starting consumer runner", topic);
            consumer.subscribe(Collections.singletonList(topic));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                logger.trace("{} polled consumer - record count = {}", topic, records.count());
                for (ConsumerRecord<String, String> record : records) {
                    logger.info("{} - offset = {}, key = {}, value = {}}", topic, record.offset(), record.key(), record.value());
                    consumingProcessor.process(record.value());
                }
                consumer.commitAsync();
            }
        } catch (WakeupException e) {
            logger.info("[{}] Waked up ", topic);
        } finally {
            consumer.close();
            logger.info("[{}] finished to close consumer runner", topic);
        }
    }

    public void wakeup() {
        consumer.wakeup();
    }
}
