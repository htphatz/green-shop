package com.dev.backend.dto.request;

import com.dev.backend.enums.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangeOrderStatusReq {
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
