package com.dev.backend.scheduler;

import com.dev.backend.service.ProductRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Refresh-Ahead / Ahead-of-Time Caching Scheduler.
 * Proactively pre-warms Cache for default product catalog pages
 * prior to TTL expiration, guaranteeing 0ms access latency for end users.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CachePrewarmScheduler {
    private final ProductRedisService productRedisService;

    @Scheduled(cron = "0 */8 * * * *") // Runs every 8 minutes (before 10-minute TTL expires)
    public void prewarmHotProductsCache() {
        log.info("Executing Refresh-Ahead pre-warming for default product catalog page...");
        try {
            // Pre-warm page 1 of default product catalog
            productRedisService.searchProductsRedis(null, null, "asc", 1, 10);
            log.info("Refresh-Ahead cache pre-warming completed successfully.");
        } catch (Exception e) {
            log.error("Failed to pre-warm product cache: {}", e.getMessage(), e);
        }
    }
}
