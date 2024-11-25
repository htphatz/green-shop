package com.dev.backend.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PreviewOrderItemReq {
    private String productId;
    private Integer quantity;
}
