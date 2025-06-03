package com.dev.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderItemReq {
    @NotBlank(message = "Product's ID is required")
    private String productId;

    @NotNull(message = "Quantity is required")
    private Integer quantity;
}
