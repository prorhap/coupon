package com.github.prorhap.coupon.play.config;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import play.Environment;

public class EsModule extends AbstractModule {
    private final Environment environment;
    private final Config config;

    public EsModule(Environment environment, Config config) {
        this.environment = environment;
        this.config = config;
    }
    @Override
    public void configure() {
        bindConstant().annotatedWith(Names.named("indexName")).to(config.getString("elasticsearch.indexName"));
        bind(JestClient.class).annotatedWith(Names.named("defaultJestClient")).toInstance(createJestClient());
    }

    private JestClient createJestClient() {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(config.getString("elasticsearch.uri"))
                .multiThreaded(true)
                .defaultMaxTotalConnectionPerRoute(config.getInt("elasticsearch.defaultMaxTotalConnectionPerRoute"))
                .maxTotalConnection(config.getInt("elasticsearch.maxTotalConnection"))
                .build());
        return factory.getObject();
    }
}
