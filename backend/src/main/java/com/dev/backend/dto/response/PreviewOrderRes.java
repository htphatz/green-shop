package com.dev.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PreviewOrderRes {
    private BigDecimal totalMoney;
}