package com.dev.backend.service.impl;

import com.dev.backend.dto.event.UpdateInventoryEvent;
import com.dev.backend.entity.Product;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaService {
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-events")
    public void updateInventory(String events) throws JsonProcessingException {
        List<UpdateInventoryEvent> eventsListener = objectMapper.readValue(events, new TypeReference<List<UpdateInventoryEvent>>() {});

        // Kiểm tra số lượng
        for (UpdateInventoryEvent event : eventsListener) {
            try {
                Product product = productRepository.findById(event.getProductId())
                        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
                // Kiểm tra tồn kho
                if (product.getSoldQuantity() + event.getSoldQuantity() > product.getQuantity()) {
                    throw new AppException(ErrorCode.OUT_OF_STOCK);
                }
                product.setSoldQuantity(product.getSoldQuantity() + event.getSoldQuantity());
                productRepository.save(product);

            } catch (AppException e) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }
        }
    }
}
