package com.dev.backend.dto.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateInventoryEvent {
    private String productId;
    private Integer soldQuantity;
}
