package com.dev.backend.dto.request;

import com.dev.backend.enums.PaymentMethod;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OrderReq {
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String note;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private List<OrderItemReq> orderItems;
}
