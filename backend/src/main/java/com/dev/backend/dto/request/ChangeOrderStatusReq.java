package com.dev.backend.dto.request;

import com.dev.backend.enums.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangeOrderStatusReq {
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
