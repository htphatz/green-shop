package com.dev.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ProductRes {
    private String id;
    private String name;
    private BigDecimal price;
    private String imageUrl;
    private String description;
    private Integer quantity;
    private Integer soldQuantity;
    private CategoryRes category;
}
