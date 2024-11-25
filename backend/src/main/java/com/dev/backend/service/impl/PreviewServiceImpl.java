package com.dev.backend.service.impl;

import com.dev.backend.dto.request.OrderItemReq;
import com.dev.backend.dto.request.PreviewOrderItemReq;
import com.dev.backend.dto.request.PreviewOrderReq;
import com.dev.backend.dto.response.PreviewOrderItemRes;
import com.dev.backend.dto.response.PreviewOrderRes;
import com.dev.backend.entity.Product;
import com.dev.backend.entity.Voucher;
import com.dev.backend.enums.DiscountType;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.repository.ProductRepository;
import com.dev.backend.repository.VoucherRepository;
import com.dev.backend.service.PreviewService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PreviewServiceImpl implements PreviewService {
    private final ProductRepository productRepository;
    private final VoucherRepository voucherRepository;

    @Override
    public PreviewOrderItemRes previewOrderItem(PreviewOrderItemReq request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        if (product.getSoldQuantity() + request.getQuantity() > product.getQuantity()) {
            throw new AppException(ErrorCode.OUT_OF_STOCK);
        }
        double totalMoneyOfOrderItem = product.getPrice().doubleValue() * request.getQuantity();
        return PreviewOrderItemRes.builder()
                .totalMoney(BigDecimal.valueOf(totalMoneyOfOrderItem))
                .build();
    }

    @Override
    public PreviewOrderRes previewOrder(PreviewOrderReq request) {
        double totalMoneyOfOrder = 0D;
        for (OrderItemReq orderItemReq : request.getOrderItems()) {
            Product product = productRepository.findById(orderItemReq.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            if (product.getSoldQuantity() + orderItemReq.getQuantity() > product.getQuantity()) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }
            double totalMoneyOfOrderItem = product.getPrice().doubleValue() * orderItemReq.getQuantity();
            totalMoneyOfOrder += totalMoneyOfOrderItem;
        }
        if (Strings.isNotEmpty(request.getVoucherCode())) {
            Voucher voucher = voucherRepository.findByCode(request.getVoucherCode())
                    .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
            if (voucher.getExpirationDate().isBefore(LocalDateTime.now())) {
                throw new AppException(ErrorCode.VOUCHER_EXPIRED);
            }
            if (voucher.getDiscountType() == DiscountType.FIXED) {
                totalMoneyOfOrder -= voucher.getDiscountValue();
            }
            else if (voucher.getDiscountType() == DiscountType.PERCENT) {
                totalMoneyOfOrder = totalMoneyOfOrder * (1 - voucher.getDiscountValue() / 100);
            }
        }
        totalMoneyOfOrder = Math.max(0, totalMoneyOfOrder);
        return PreviewOrderRes.builder()
                .totalMoney(BigDecimal.valueOf(totalMoneyOfOrder))
                .build();
    }
}
