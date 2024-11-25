package com.dev.backend.service.impl;

import com.dev.backend.dto.request.ChangeOrderInfoReq;
import com.dev.backend.dto.request.ChangeOrderStatusReq;
import com.dev.backend.dto.request.OrderItemReq;
import com.dev.backend.dto.request.OrderReq;
import com.dev.backend.dto.response.OrderRes;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.entity.*;
import com.dev.backend.enums.DiscountType;
import com.dev.backend.enums.OrderStatus;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.mapper.OrderItemMapper;
import com.dev.backend.mapper.OrderMapper;
import com.dev.backend.repository.*;
import com.dev.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;

    @Override
    public OrderRes createOrder(OrderReq request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Order order = orderMapper.toOrder(request);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        double totalMoneyOfOrder = 0D;
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemReq orderItemReq : request.getOrderItems()) {
            Product product = productRepository.findById(orderItemReq.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            if (product.getSoldQuantity() + orderItemReq.getQuantity() > product.getQuantity()) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }

            OrderItem orderItem = orderItemMapper.toOrderItem(orderItemReq);
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setPrice(product.getPrice());
            double totalMoneyOfOrderItem = product.getPrice().doubleValue() * orderItemReq.getQuantity();
            orderItem.setTotalMoney(BigDecimal.valueOf(totalMoneyOfOrderItem));
            orderItems.add(orderItem);
            totalMoneyOfOrder += totalMoneyOfOrderItem;
            product.setSoldQuantity(product.getSoldQuantity() + orderItemReq.getQuantity());
        }

        if (Strings.isNotEmpty(request.getVoucherCode())) {
            Voucher voucher = voucherRepository.findByCode(request.getVoucherCode())
                    .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
            if (voucher.getExpirationDate().isBefore(LocalDateTime.now())) {
                throw new AppException(ErrorCode.VOUCHER_EXPIRED);
            }
            if (voucher.getDiscountType() == DiscountType.FIXED) {
                totalMoneyOfOrder -= voucher.getDiscountValue();
            } else if (voucher.getDiscountType() == DiscountType.PERCENT) {
                totalMoneyOfOrder *= (1 - voucher.getDiscountValue() / 100);
            }
        }

        totalMoneyOfOrder = Math.max(0, totalMoneyOfOrder);
        order.setTotalMoney(BigDecimal.valueOf(totalMoneyOfOrder));
        Order newOrder = orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);
        productRepository.saveAll(orderItems.stream().map(OrderItem::getProduct).toList());
        return orderMapper.toOrderRes(newOrder);
    }


    @Override
    public OrderRes getOrderById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        return orderMapper.toOrderRes(order);
    }

    @Override
    public PageDto<OrderRes> getAllOrders(Integer pageNumber, Integer pageSize) {
        pageNumber--;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Order> orders = orderRepository.findAll(pageable);
        return PageDto.of(orders).map(orderMapper::toOrderRes);
    }

    @Override
    public OrderRes updateOrderStatus(String id, ChangeOrderStatusReq request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setStatus(request.getStatus());
        return orderMapper.toOrderRes(orderRepository.save(order));
    }

    @Override
    public OrderRes updateOrderInfo(String id, ChangeOrderInfoReq request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setFullName(request.getFullName());
        order.setAddress(request.getAddress());
        order.setPhone(request.getPhone());
        return orderMapper.toOrderRes(orderRepository.save(order));
    }

    @Override
    public PageDto<OrderRes> searchOrders(OrderStatus orderStatus, String userId, Integer pageNumber, Integer pageSize) {
        pageNumber--;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Order> orders = orderRepository.searchOrders(orderStatus, userId, pageable);
        return PageDto.of(orders).map(orderMapper::toOrderRes);
    }

    @Override
    public PageDto<OrderRes> getMyOrders(Integer pageNumber, Integer pageSize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        pageNumber--;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Order> orders = orderRepository.findByUserId(user.getId(), pageable);
        return PageDto.of(orders).map(orderMapper::toOrderRes);
    }
}
