package com.github.prorhap.coupon.play.helper;

import com.google.inject.ImplementedBy;

import java.util.concurrent.CompletionStage;

@ImplementedBy(Memcache.class)
public interface RemoteCache {
    CompletionStage<String> get(String key);

//    List<Observable<KeyValuePair<String, MemcacheStatus>>>  delete(List<String> keys);

//    Observable<KeyValuePair<String, MemcacheStatus>> delete(String key);

    void set(String key, String value);
}
