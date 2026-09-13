package com.dev.backend.service;

import com.dev.backend.dto.response.ProductRes;
import com.dev.backend.entity.Product;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.mapper.ProductMapper;
import com.dev.backend.repository.ProductRepository;
import com.dev.backend.service.impl.BaseRedisServiceImpl;
import com.dev.backend.service.impl.ProductRedisServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRedisServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BaseRedisService<String, String, String> baseRedisService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RLock lock;

    @InjectMocks
    private ProductRedisServiceImpl productRedisService;

    @Test
    @DisplayName("Cache Hit: Return product from Redis without querying Database")
    void getProductRedisById_CacheHit() throws Exception {
        String productId = "prod-1";
        String redisKey = "product:prod-1";
        String cachedJson = "{\"id\":\"prod-1\",\"name\":\"Fiddle Leaf Fig\"}";

        when(baseRedisService.get(redisKey)).thenReturn(cachedJson);

        ProductRes expectedRes = ProductRes.builder().id("prod-1").name("Fiddle Leaf Fig").build();
        when(objectMapper.readValue(cachedJson, ProductRes.class)).thenReturn(expectedRes);

        ProductRes result = productRedisService.getProductRedisById(productId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Fiddle Leaf Fig");
        verify(productRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Cache Penetration: Throws PRODUCT_NOT_FOUND when NULL_SENTINEL is stored")
    void getProductRedisById_CachePenetration_SentinelHit() {
        String productId = "prod-not-exist";
        String redisKey = "product:prod-not-exist";

        when(baseRedisService.get(redisKey)).thenReturn(BaseRedisServiceImpl.NULL_SENTINEL);

        AppException ex = assertThrows(AppException.class, () -> productRedisService.getProductRedisById(productId));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
        verify(productRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Cache Miss: Acquires distributed lock, queries DB, populates cache with jitter")
    void getProductRedisById_CacheMiss_SuccessRebuild() throws Exception {
        String productId = "prod-1";
        String redisKey = "product:prod-1";
        String lockKey = "lock:product:prod-1";

        when(baseRedisService.get(redisKey)).thenReturn(null);
        when(baseRedisService.getLock(lockKey)).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(true);

        Product product = Product.builder()
                .id("prod-1")
                .name("Snake Plant")
                .price(BigDecimal.valueOf(200000))
                .build();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductRes res = ProductRes.builder().id("prod-1").name("Snake Plant").build();
        when(productMapper.toProductRes(product)).thenReturn(res);
        when(objectMapper.writeValueAsString(res)).thenReturn("{\"id\":\"prod-1\"}");

        ProductRes result = productRedisService.getProductRedisById(productId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Snake Plant");
        verify(baseRedisService, times(1)).setWithRandomJitter(eq(redisKey), anyString(), eq(10L), eq(5L));
        verify(lock, times(1)).unlock();
    }

    @Test
    @DisplayName("Cache Miss & DB Not Found: Stores NULL Sentinel to prevent penetration")
    void getProductRedisById_CacheMiss_DBNotFound_StoresSentinel() throws Exception {
        String productId = "prod-missing";
        String redisKey = "product:prod-missing";
        String lockKey = "lock:product:prod-missing";

        when(baseRedisService.get(redisKey)).thenReturn(null);
        when(baseRedisService.getLock(lockKey)).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(true);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> productRedisService.getProductRedisById(productId));
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
        verify(baseRedisService, times(1)).setNullValue(redisKey, 3L);
        verify(lock, times(1)).unlock();
    }

    @Test
    @DisplayName("Increment View Count: Atomically increment hash field in Redis")
    void incrementProductViewCount_Success() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        productRedisService.incrementProductViewCount("prod-1");

        verify(hashOperations, times(1)).increment("product:views", "prod-1", 1L);
    }

    @Test
    @DisplayName("Clear Product Cache: Invalidate Redis key on update/delete")
    void clearProductCache_Success() {
        productRedisService.clearProductCache("prod-1");

        verify(baseRedisService, times(1)).delete("product:prod-1");
    }
}
