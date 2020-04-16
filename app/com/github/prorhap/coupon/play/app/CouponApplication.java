package com.github.prorhap.coupon.play.app;

import com.github.prorhap.coupon.play.config.DynamoModule;
import com.github.prorhap.coupon.play.config.EsModule;
import com.github.prorhap.coupon.play.config.KafkaModule;
import com.github.prorhap.coupon.play.config.RemoteCacheModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CouponApplication {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ConsumerRunner couponCreationConsumerRunner;
    private final ConsumerRunner couponIssueConsumerRunner;
    private final ConsumerRunner couponUseConsumerRunner;
    private final ConsumerRunner couponCancelConsumerRunner;

    @Inject
    public CouponApplication(@Named("couponCreationConsumerRunner") ConsumerRunner couponCreationConsumerRunner,
                             @Named("couponIssueConsumerRunner") ConsumerRunner couponIssueConsumerRunner,
                             @Named("couponUseConsumerRunner") ConsumerRunner couponUseConsumerRunner,
                             @Named("couponCancelConsumerRunner") ConsumerRunner couponCancelConsumerRunner) {

        this.couponCreationConsumerRunner = couponCreationConsumerRunner;
        this.couponIssueConsumerRunner = couponIssueConsumerRunner;
        this.couponUseConsumerRunner = couponUseConsumerRunner;
        this.couponCancelConsumerRunner = couponCancelConsumerRunner;
    }

    public void start() {

        List<ConsumerRunner> consumerRunners =
                Arrays.asList(couponCreationConsumerRunner, couponIssueConsumerRunner, couponUseConsumerRunner, couponCancelConsumerRunner);

        logger.info("Run consumer runners");
        ExecutorService executorService = Executors.newFixedThreadPool(consumerRunners.size());
        consumerRunners.forEach(consumerRunner -> executorService.submit(consumerRunner));

        logger.info("Adding shutdownhook");
        CouponApplicationShutdowner shutdowner = new CouponApplicationShutdowner(consumerRunners);
        Runtime.getRuntime().addShutdownHook(new Thread(shutdowner));

        logger.info("Started ");
    }

    public static void main(String[] args) {

        Config config = ConfigFactory.load();

        Injector injector = Guice.createInjector(new KafkaModule(null, config), new DynamoModule(null, config), new RemoteCacheModule(null, config), new EsModule(null, config));
        injector.getInstance(CouponApplication.class).start();
    }
}
