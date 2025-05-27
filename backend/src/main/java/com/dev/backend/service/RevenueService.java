package com.dev.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import com.dev.backend.dto.response.RevenueRes;

public interface RevenueService {
  RevenueRes getRevenue(LocalDateTime startDate, LocalDateTime endDate);
  RevenueRes getRevenueByMonth(Integer month, Integer year);
  RevenueRes getRevenueByYear(Integer year);
  List<Object[]> getRevenueByProduct();
  List<Object[]> getRevenueByCategory();
}

