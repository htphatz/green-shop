package com.dev.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PreviewOrderReq {
    private String voucherCode;

    @NotNull(message = "Order items is required")
    private List<OrderItemReq> orderItems;
}
