package com.github.prorhap.coupon.play.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CouponApplicationShutdowner implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private List<ConsumerRunner> consumerRunners;

    public CouponApplicationShutdowner(List<ConsumerRunner> consumerRunners) {
        this.consumerRunners = consumerRunners;
    }

    @Override
    public void run() {
        consumerRunners.forEach(consumerRunner -> consumerRunner.wakeup());
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            logger.error("", e);
        }
    }
}
