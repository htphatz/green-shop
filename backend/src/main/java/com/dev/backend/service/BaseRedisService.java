package com.dev.backend.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BaseRedisService <K, F, V> {
    void set(K key, V value);
    void setTimeToLive(K key, Long timeoutInDays);
    void hashSet(K key, F field, V value);
    boolean hashExists(K key, F field);
    V get(K key);
    Map<F, V> getField(K key);
    V hashGet(K key, F field);
    List<V> hashGetByFieldPrefix(K key, String fieldPrefix);
    Set<String> getFieldPrefixes(K key);
    void delete(K key);
    void delete(K key, F field);
    void delete(K key, List<F> fields);
}
