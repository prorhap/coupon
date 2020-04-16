package com.github.prorhap.coupon.play.helper;

import com.github.prorhap.coupon.play.common.CouponDateFormatConstant;
import com.github.prorhap.coupon.play.model.Coupon;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EsClient implements CouponDateFormatConstant {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    private final JestClient client;
    private final String indexName;

    @Inject
    public EsClient(@Named("defaultJestClient") JestClient client, @Named("indexName") String indexName) {
        this.client = client;
        this.indexName = indexName;
    }

    public Coupon getCouponByCouponCode(String couponCode) {
        try {
            Get get = new Get.Builder(indexName, couponCode).build();
            return client.execute(get).getSourceAsObject(Coupon.class);
        } catch (IOException e) {
            logger.error("Error while getting from elasticsearch", e);
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<List<Coupon>> getCouponByUserIdAsync(String couponCode) {
        try {
            String queryTemplate = "{\n" +
                    "    \"query\": {\n" +
                    "        \"match\" : {\n" +
                    "            \"userId\" : \"%s\"\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
            Search search = new Search.Builder(String.format(queryTemplate, couponCode))
                    .addIndex(indexName)
                    .build();

            CompletableFuture<List<Coupon>> completableFuture = new CompletableFuture<>();
            client.executeAsync(search, new JestResultHandler<SearchResult>() {
                @Override
                public void completed(SearchResult result) {
                    if (!result.isSucceeded()) {
                        completableFuture.completeExceptionally(new Exception(result.getErrorMessage()));
                    } else {
                        List<Coupon> coupons = result.getSourceAsObjectList(Coupon.class, false);
                        logger.debug("fetched coupons = {}", coupons);
                        completableFuture.complete(coupons);
                    }
                }

                @Override
                public void failed(Exception e) {
                    logger.error("Error", e);
                    completableFuture.completeExceptionally(e);
                }
            });
            return completableFuture;
        } catch (Exception e) {
            logger.error("Error while searching from elasticsearch", e);
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<List<Coupon>> getCouponByExpireDateAndUnusedAsync(Date date) {
        try {
            String queryTemplate = "{\n" +
                    "  \"query\": {\n" +
                    "    \"bool\": {\n" +
                    "      \"must\": [\n" +
                    "        {\n" +
                    "          \"match\": {\n" +
                    "            \"expireAt\": \"%s\"\n" +
                    "          }\n" +
                    "        },\n" +
//                    "        {\n" +
//                    "          \"match\": {\n" +
//                    "            \"issued\": true\n" +
//                    "          }\n" +
//                    "        },\n" +
                    "        {\n" +
                    "          \"match\": {\n" +
                    "            \"used\": false\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            String query = String.format(queryTemplate, dateFormat.format(date));
            Search search = new Search
                    .Builder(query)
                    .addIndex(indexName)
                    .build();

            CompletableFuture<List<Coupon>> completableFuture = new CompletableFuture<>();
            client.executeAsync(search, new JestResultHandler<SearchResult>() {
                @Override
                public void completed(SearchResult result) {
                    if (!result.isSucceeded()) {
                        completableFuture.completeExceptionally(new Exception(result.getErrorMessage()));
                    } else {
                        List<Coupon> coupons = result.getSourceAsObjectList(Coupon.class, false);
                        logger.debug("fetched coupons = {}", coupons);
                        completableFuture.complete(coupons);
                    }
                }

                @Override
                public void failed(Exception e) {
                    logger.error("Error", e);
                    completableFuture.completeExceptionally(e);
                }
            });
            return completableFuture;
        } catch (Exception e) {
            logger.error("Error while searching from elasticsearch", e);
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<List<Coupon>> getCouponByExpireDateAndUnusedAndIssuedAsync(Date date) {
        try {
            String queryTemplate = "{\n" +
                    "  \"query\": {\n" +
                    "    \"bool\": {\n" +
                    "      \"must\": [\n" +
                    "        {\n" +
                    "          \"match\": {\n" +
                    "            \"expireAt\": \"%s\"\n" +
                    "          }\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"match\": {\n" +
                    "            \"issued\": true\n" +
                    "          }\n" +
                    "        },\n" +
                    "        {\n" +
                    "          \"match\": {\n" +
                    "            \"used\": false\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            String query = String.format(queryTemplate, dateFormat.format(date));
            Search search = new Search
                    .Builder(query)
                    .addIndex(indexName)
                    .build();

            CompletableFuture<List<Coupon>> completableFuture = new CompletableFuture<>();
            client.executeAsync(search, new JestResultHandler<SearchResult>() {
                @Override
                public void completed(SearchResult result) {
                    if (!result.isSucceeded()) {
                        completableFuture.completeExceptionally(new Exception(result.getErrorMessage()));
                    } else {
                        List<Coupon> coupons = result.getSourceAsObjectList(Coupon.class, false);
                        logger.debug("fetched coupons = {}", coupons);
                        completableFuture.complete(coupons);
                    }
                }

                @Override
                public void failed(Exception e) {
                    logger.error("Error", e);
                    completableFuture.completeExceptionally(e);
                }
            });
            return completableFuture;
        } catch (Exception e) {
            logger.error("Error while searching from elasticsearch", e);
            throw new RuntimeException(e);
        }
    }

    public void index(Coupon source) {
        try {
            logger.debug("index to {} = {} ", indexName, source);
            Index index = createIndex(source);
            JestResult result = client.execute(index);
            logger.debug("result = {}", result);
        } catch (IOException e) {
            logger.error("Error while searching from elasticsearch", e);
            throw new RuntimeException(e);
        }
    }

    public void indexAsync(Coupon source) {
        Index index = createIndex(source);

        client.executeAsync(index, new JestResultHandler<DocumentResult>() {
            @Override
            public void completed(DocumentResult result) {
                logger.debug("finish to index {}", result.toString());
            }

            @Override
            public void failed(Exception e) {
                logger.error("Error while indexing " + source.getCouponCode(), e);
            }
        });
    }

    private Index createIndex(Coupon source) {
        return new Index.Builder(source)
                .index(indexName)
                .type("_doc")
                .id(source.getCouponCode())
                .build();
    }
}
