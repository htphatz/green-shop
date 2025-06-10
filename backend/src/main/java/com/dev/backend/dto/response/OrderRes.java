package com.dev.backend.dto.response;

import com.dev.backend.enums.OrderStatus;
import com.dev.backend.enums.PaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class OrderRes {
    private String id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String note;
    private BigDecimal totalMoney;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private List<OrderItemRes> orderItems;
}
