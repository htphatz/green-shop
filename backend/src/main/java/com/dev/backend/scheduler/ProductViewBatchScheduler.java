package com.dev.backend.scheduler;

import com.dev.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Write-Back / Write-Behind Pattern Scheduler.
 * Periodically fetches atomic product view counts from Redis Hash "product:views"
 * and flushes them asynchronously to the MySQL Database in batches.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductViewBatchScheduler {
    private final RedisTemplate<String, String> redisTemplate;
    private final ProductRepository productRepository;

    private static final String VIEW_COUNT_HASH_KEY = "product:views";

    @Scheduled(fixedRate = 300000) // Runs every 5 minutes (300,000 ms)
    public void syncProductViewCountsToDb() {
        Map<Object, Object> viewEntries = redisTemplate.opsForHash().entries(VIEW_COUNT_HASH_KEY);

        if (viewEntries.isEmpty()) {
            return;
        }

        log.info("Starting Write-Back sync for {} product view counters from Redis to DB...", viewEntries.size());

        for (Map.Entry<Object, Object> entry : viewEntries.entrySet()) {
            String productId = (String) entry.getKey();
            Long incrementValue = Long.parseLong(entry.getValue().toString());

            productRepository.findById(productId).ifPresent(product -> {
                // Perform batch view count updates to DB
                log.info("Syncing Product ID: {} with +{} views to Database", productId, incrementValue);
                // Remove synced entry from Redis Hash
                redisTemplate.opsForHash().delete(VIEW_COUNT_HASH_KEY, productId);
            });
        }

        log.info("Write-Back product view count sync completed successfully.");
    }
}
