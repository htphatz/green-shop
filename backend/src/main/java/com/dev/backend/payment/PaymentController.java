package com.dev.backend.payment;

import com.dev.backend.dto.request.ChangeOrderStatusReq;
import com.dev.backend.dto.request.OrderReq;
import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.dto.response.OrderRes;
import com.dev.backend.enums.OrderStatus;
import com.dev.backend.enums.PaymentMethod;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.payment.vnpay.VNPayService;
import com.dev.backend.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("payment")
@RequiredArgsConstructor
public class PaymentController {
    private final VNPayService vnPayService;
    private final OrderService orderService;

    @GetMapping("pay")
    public APIResponse<?> pay(@RequestBody OrderReq request) {
        if (request.getPaymentMethod() == PaymentMethod.CASH) {
            OrderRes result = orderService.createOrder(request);
            return APIResponse.<OrderRes>builder().result(result).build();
        } else if (request.getPaymentMethod() == PaymentMethod.VN_PAY) {
            PaymentDto result = vnPayService.createVnPayPayment(request);
            return APIResponse.<PaymentDto>builder().result(result).build();
        } else {
            throw new AppException(ErrorCode.PAYMENT_INVALID);
        }
    }

    @GetMapping("vn-pay-callback")
    public APIResponse<?> payCallbackHandler(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        String orderId = request.getParameter("vnp_TxnRef");
        if (status.equals("00")) {
            OrderRes result = extractOrderIdFromCallback(orderId);
            return APIResponse.<PaymentDto>builder().code(HttpStatus.OK.value()).message("Payment successful").build();
        } else {
            deleteOrderFromCallback(orderId);
            return APIResponse.<PaymentDto>builder().code(HttpStatus.BAD_REQUEST.value()).message("Payment failed").build();
        }
    }

    private OrderRes extractOrderIdFromCallback(String orderId) {
        ChangeOrderStatusReq changeOrderStatusReq = ChangeOrderStatusReq.builder()
                .status(OrderStatus.PAID)
                .build();
        return orderService.updateOrderStatus(orderId, changeOrderStatusReq);
    }

    private void deleteOrderFromCallback(String orderId) {
        orderService.deleteOrder(orderId);
    }
}
