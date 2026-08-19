package com.dev.backend.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSyncEvent {
    public enum EventType { CREATE, UPDATE, DELETE }

    private EventType eventType;
    private String productId;
    private String name;
    private BigDecimal price;
    private String description;
    private Integer quantity;
    private Integer soldQuantity;
    private String categoryId;
    private String categoryName;
    private String imageUrl;
}
