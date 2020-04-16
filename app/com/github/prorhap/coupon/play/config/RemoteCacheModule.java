package com.github.prorhap.coupon.play.config;

import com.github.prorhap.coupon.play.helper.Memcache;
import com.github.prorhap.coupon.play.helper.RemoteCache;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.spotify.folsom.MemcacheClient;
import com.spotify.folsom.MemcacheClientBuilder;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Environment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RemoteCacheModule extends AbstractModule {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Environment environment;
    private final Config config;

    public RemoteCacheModule(Environment environment, Config config) {
        this.environment = environment;
        this.config = config;
    }
    @Override
    protected void configure() {
        bind(RemoteCache.class).toInstance(createMemcacheRemoteCache());
        bind(ExecutorService.class).annotatedWith(Names.named("remoteCacheAsyncExecutor")).toInstance(Executors.newFixedThreadPool(2));
    }

    private RemoteCache createMemcacheRemoteCache() {
        return new Memcache(createMemcacheClient(), config.getInt("memcache.ttl"));
    }

    private MemcacheClient createMemcacheClient() {
        logger.info("creating memcache client for {}:{}", config.getString("memcache.host"), config.getInt("memcache.port"));
        MemcacheClientBuilder<String> stringMemcacheClientBuilder = MemcacheClientBuilder.newStringClient();
        stringMemcacheClientBuilder.withAddress(config.getString("memcache.host"), config.getInt("memcache.port"));
        return stringMemcacheClientBuilder
                .withConnections(config.getInt("memcache.connections"))
                .withMaxOutstandingRequests(config.getInt("memcache.maxOutstandingRequests"))
                .withRetry(config.getBoolean("memcache.retry"))
                .withRequestTimeoutMillis(config.getInt("memcache.requestTimeout.ms"))
                .connectAscii();

    }
}
