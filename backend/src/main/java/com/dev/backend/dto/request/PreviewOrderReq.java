package com.dev.backend.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PreviewOrderReq {
    private String voucherCode;
    private List<OrderItemReq> orderItems;
}
