package com.dev.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class OrderItemRes {
    private String productId;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalMoney;
}
