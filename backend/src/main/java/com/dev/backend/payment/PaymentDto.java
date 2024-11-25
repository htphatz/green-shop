package com.dev.backend.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDto {
    private String code;
    private String message;
    private String paymentUrl;
}
