package com.dev.backend.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItemReq {
    private String productId;
    private Integer quantity;
}