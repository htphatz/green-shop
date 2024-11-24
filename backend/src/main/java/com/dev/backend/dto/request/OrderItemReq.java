package com.dev.backend.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class OrderItemReq {
    private String productId;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalMoney;
}
