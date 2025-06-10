package com.dev.backend.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentStatusUpdateReq {
    private String responseCode;
    private String orderId;
    private String amount;
} 