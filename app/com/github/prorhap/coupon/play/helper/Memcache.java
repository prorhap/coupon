package com.github.prorhap.coupon.play.helper;

import com.spotify.folsom.MemcacheClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

public class Memcache implements RemoteCache {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MemcacheClient<String> memcacheClient;
    private final Integer ttl;

    @Inject
    public Memcache(MemcacheClient<String> memcacheClient, Integer ttl) {
        this.memcacheClient = memcacheClient;
        this.ttl = ttl;
    }

    @Override
    public CompletionStage<String> get(String key) {
        return memcacheClient.get(key);
    }

    @Override
    public void set(String key, String value) {
        logger.debug("set data into memcache for {}", key);
        memcacheClient.set(key, value, ttl)
                .whenComplete((memcacheStatus, throwable) -> {
                    if (Objects.nonNull(throwable)) {
                        logger.error("Error while setting data into memcache for " + key+ " / "+throwable.getMessage(), throwable);
                    } else {
                        logger.trace("memcache set result = {}", memcacheStatus);
                    }
                });
    }
}
