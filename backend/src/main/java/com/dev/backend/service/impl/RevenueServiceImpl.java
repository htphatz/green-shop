package com.dev.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import com.dev.backend.dto.response.RevenueRes;
import com.dev.backend.repository.OrderRepository;
import com.dev.backend.service.RevenueService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RevenueServiceImpl implements RevenueService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public RevenueRes getRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        if (endDate.isBefore(startDate)) {
            throw new AppException(ErrorCode.DATE_INVALID);
        }

        BigDecimal revenue = orderRepository.getRevenue(startDate, endDate);
        return RevenueRes.builder()
                .revenue(revenue != null ? revenue : BigDecimal.ZERO)
                .build();
    }

    @Override
    public RevenueRes getRevenueByMonth(Integer month, Integer year) {
        return RevenueRes.builder()
                .revenue(orderRepository.getRevenueByMonth(month, year))
                .build();
    }

    @Override
    public RevenueRes getRevenueByYear(Integer year) {
        return RevenueRes.builder()
                .revenue(orderRepository.getRevenueByYear(year))
                .build();
    }

    @Override
    public List<Object[]> getRevenueByProduct(LocalDateTime startDate, LocalDateTime endDate) {
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        if (endDate.isBefore(startDate)) {
            throw new AppException(ErrorCode.DATE_INVALID);
        }

        return orderItemRepository.getRevenueByProduct(startDate, endDate);
    }

    @Override
    public List<Object[]> getRevenueByCategory(LocalDateTime startDate, LocalDateTime endDate) {
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        if (endDate.isBefore(startDate)) {
            throw new AppException(ErrorCode.DATE_INVALID);
        }

        return orderItemRepository.getRevenueByCategory(startDate, endDate);
    }
}
