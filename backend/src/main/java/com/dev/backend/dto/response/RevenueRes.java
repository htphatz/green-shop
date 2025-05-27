package com.dev.backend.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RevenueRes {
  private BigDecimal revenue;
}
