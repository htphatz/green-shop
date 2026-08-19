package com.dev.backend.service.kafka;

import com.dev.backend.document.ProductDocument;
import com.dev.backend.dto.event.ProductSyncEvent;
import com.dev.backend.repository.elasticsearch.ProductElasticRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductElasticConsumer {
    private final ProductElasticRepository productElasticRepository;
    private final ObjectMapper objectMapper;

    /** Listen to product update/delete events from Kafka and synchronize to Elasticsearch asynchronously **/
    @KafkaListener(topics = "product-sync-topic", groupId = "es-product-sync-group")
    public void handleProductSync(String message) {
        try {
            ProductSyncEvent event = objectMapper.readValue(message, ProductSyncEvent.class);
            log.info("Processing ES Sync Event: type={}, id={}", event.getEventType(), event.getProductId());

            if (event.getEventType() == ProductSyncEvent.EventType.DELETE) {
                productElasticRepository.deleteById(event.getProductId());
            } else {
                ProductDocument doc = ProductDocument.builder()
                        .id(event.getProductId())
                        .name(event.getName())
                        .price(event.getPrice())
                        .description(event.getDescription())
                        .quantity(event.getQuantity())
                        .soldQuantity(event.getSoldQuantity())
                        .categoryId(event.getCategoryId())
                        .categoryName(event.getCategoryName())
                        .imageUrl(event.getImageUrl())
                        .build();
                productElasticRepository.save(doc);
            }
        } catch (Exception e) {
            log.error("Failed to process ES sync event for message: {}", message, e);
            // Optional: Publish failed messages to Dead Letter Queue (DLQ)
        }
    }
}
