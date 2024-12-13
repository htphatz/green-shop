package com.dev.backend.payment.vnpay;

import com.dev.backend.dto.request.OrderItemReq;
import com.dev.backend.dto.request.OrderReq;
import com.dev.backend.dto.response.OrderRes;
import com.dev.backend.entity.Product;
import com.dev.backend.entity.Voucher;
import com.dev.backend.enums.DiscountType;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.payment.PaymentDto;
import com.dev.backend.repository.ProductRepository;
import com.dev.backend.repository.VoucherRepository;
import com.dev.backend.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VNPayService {
    private final VNPayConfig vnPayConfig;
    private final ProductRepository productRepository;
    private final VoucherRepository voucherRepository;
    private final OrderService orderService;

    public PaymentDto createVnPayPayment(OrderReq request) throws JsonProcessingException {
        OrderRes order = orderService.createOrder(request);
        Long amount = calculateAmount(request) * 100L;
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        vnpParamsMap.put("vnp_TxnRef", order.getId());
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnpPayUrl() + "?" + queryUrl;
        return PaymentDto.builder()
                .paymentUrl(paymentUrl)
                .build();
    }

    private Long calculateAmount(OrderReq request) {
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
        return (long) totalMoneyOfOrder;
    }
}
