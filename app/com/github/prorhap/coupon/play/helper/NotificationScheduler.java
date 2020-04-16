package com.github.prorhap.coupon.play.helper;

import akka.actor.ActorSystem;
import com.github.prorhap.coupon.play.service.CouponNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class NotificationScheduler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final int jobInterval = 1;
    private final int initialDelay = 1;
    private final ActorSystem actorSystem;
    private final ExecutionContext executionContext;

    private final CouponNotificationService couponNotificationService;

    @Inject
    public NotificationScheduler(
                                 ActorSystem actorSystem,
                                 ExecutionContext executionContext,
                                 CouponNotificationService couponNotificationService) {
        this.actorSystem = actorSystem;
        this.executionContext = executionContext;
        this.couponNotificationService = couponNotificationService;

        this.initialize();
    }

    private void initialize() {

        logger.info("starting NotificationScheduler");
        this.actorSystem.scheduler().schedule(
                Duration.create(initialDelay, TimeUnit.DAYS),
                Duration.create(jobInterval, TimeUnit.DAYS),
                () -> {
                    couponNotificationService.sendNotification(3);
                },
                this.executionContext);
    }
}