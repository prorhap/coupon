package com.github.prorhap.coupon.play.config;

import com.github.prorhap.coupon.play.helper.NotificationScheduler;
import com.google.inject.AbstractModule;

public class DefaultPlayModule extends AbstractModule {

    @Override
    public void configure() {
        bind(NotificationScheduler.class).asEagerSingleton();
    }
}
