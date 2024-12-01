package com.dev.backend.dto.request;

import com.dev.backend.enums.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangeOrderStatusReq {
    @NotBlank(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
