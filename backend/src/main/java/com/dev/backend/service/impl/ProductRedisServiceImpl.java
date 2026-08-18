package com.dev.backend.service.impl;

import com.dev.backend.dto.response.PageDto;
import com.dev.backend.dto.response.ProductRes;
import com.dev.backend.entity.Product;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.mapper.ProductMapper;
import com.dev.backend.repository.ProductRepository;
import com.dev.backend.service.BaseRedisService;
import com.dev.backend.service.ProductRedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductRedisServiceImpl implements ProductRedisService {
    private final ProductRepository productRepository;
    private final BaseRedisService<String, String, String> baseRedisService;
    private final RedisTemplate<String, String> redisTemplate;
    private final ProductMapper productMapper;
    private final ObjectMapper objectMapper;

    private static final String VIEW_COUNT_HASH_KEY = "product:views";

    @Override
    public ProductRes getProductRedisById(String id) throws JsonProcessingException {
        String key = String.format("product:%s", id);
        String cachedJson = baseRedisService.get(key);

        // 1. Prevent Cache Penetration: Check if the stored value is NULL Sentinel
        if (BaseRedisServiceImpl.NULL_SENTINEL.equals(cachedJson)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        if (cachedJson != null) {
            return objectMapper.readValue(cachedJson, ProductRes.class);
        }

        // 2. Prevent Cache Breakdown: Use Redisson Distributed Lock to rebuild cache
        String lockKey = "lock:product:" + id;
        RLock lock = baseRedisService.getLock(lockKey);
        try {
            if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                try {
                    // Double-check cache after acquiring lock
                    cachedJson = baseRedisService.get(key);
                    if (BaseRedisServiceImpl.NULL_SENTINEL.equals(cachedJson)) {
                        throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                    }
                    if (cachedJson != null) {
                        return objectMapper.readValue(cachedJson, ProductRes.class);
                    }

                    // Query Database
                    Product product = productRepository.findById(id).orElse(null);
                    if (product == null) {
                        // Store NULL Value for 3 minutes to prevent Penetration
                        baseRedisService.setNullValue(key, 3L);
                        throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
                    }

                    ProductRes result = productMapper.toProductRes(product);
                    String json = objectMapper.writeValueAsString(result);

                    // 3. Prevent Cache Avalanche: Set base TTL of 10m + Random Jitter of 0-5m
                    baseRedisService.setWithRandomJitter(key, json, 10L, 5L);
                    return result;
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            } else {
                // Threads failing to acquire lock wait 50ms and retry fetching from cache
                Thread.sleep(50);
                return getProductRedisById(id);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting for lock", e);
        }
    }

    @Override
    public PageDto<ProductRes> searchProductsRedis(String keyword, String categoryId, String sortDir, Integer pageNumber, Integer pageSize) throws JsonProcessingException {
        String key = getKey(keyword, categoryId, pageNumber, pageSize);
        String cachedJson = baseRedisService.get(key);

        if (cachedJson != null) {
            return objectMapper.readValue(cachedJson, new TypeReference<PageDto<ProductRes>>() {});
        }

        // Use Distributed Lock to rebuild search cache
        String lockKey = "lock:search:" + key;
        RLock lock = baseRedisService.getLock(lockKey);
        try {
            if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                try {
                    cachedJson = baseRedisService.get(key);
                    if (cachedJson != null) {
                        return objectMapper.readValue(cachedJson, new TypeReference<PageDto<ProductRes>>() {});
                    }

                    int pageIndex = Math.max(0, pageNumber - 1);
                    Pageable pageable = PageRequest.of(pageIndex, pageSize);
                    Page<Product> products = productRepository.searchProducts(keyword, categoryId, sortDir, pageable);
                    PageDto<ProductRes> result = PageDto.of(products).map(productMapper::toProductRes);
                    String json = objectMapper.writeValueAsString(result);

                    // Prevent Cache Avalanche via Random Jitter
                    baseRedisService.setWithRandomJitter(key, json, 10L, 3L);
                    return result;
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            } else {
                Thread.sleep(50);
                return searchProductsRedis(keyword, categoryId, sortDir, pageNumber, pageSize);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting for search lock", e);
        }
    }

    @Override
    public void incrementProductViewCount(String id) {
        // Write-Back Pattern: Increment product view count on Redis Hash atomically
        redisTemplate.opsForHash().increment(VIEW_COUNT_HASH_KEY, id, 1L);
    }

    @Override
    public void clearProductCache(String id) {
        // Cache Eviction Pattern: Invalidate product cache upon Update / Delete
        baseRedisService.delete("product:" + id);
        log.info("Cleared Redis cache for product id: {}", id);
    }

    private String getKey(String keyword, String categoryId, Integer pageNumber, Integer pageSize) {
        return String.format("all_products:%s:%s:%s:%s", keyword, categoryId, pageNumber, pageSize);
    }
}
