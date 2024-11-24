package com.dev.backend.service.impl;

import com.dev.backend.dto.request.ChangeOrderInfoReq;
import com.dev.backend.dto.request.ChangeOrderStatusReq;
import com.dev.backend.dto.request.OrderReq;
import com.dev.backend.dto.response.OrderRes;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.entity.Order;
import com.dev.backend.entity.OrderItem;
import com.dev.backend.entity.Product;
import com.dev.backend.entity.User;
import com.dev.backend.enums.OrderStatus;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.mapper.OrderItemMapper;
import com.dev.backend.mapper.OrderMapper;
import com.dev.backend.repository.OrderItemRepository;
import com.dev.backend.repository.OrderRepository;
import com.dev.backend.repository.ProductRepository;
import com.dev.backend.repository.UserRepository;
import com.dev.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;

    @Override
    public OrderRes createOrder(OrderReq request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Order order = orderMapper.toOrder(request);
        User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        request.getOrderItems().stream().forEach(orderItemReq -> {
            OrderItem orderItem = orderItemMapper.toOrderItem(orderItemReq);
            orderItem.setOrder(order);
            Product product = productRepository.findById(orderItemReq.getProductId())
                            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            orderItem.setProduct(product);
            orderItemRepository.save(orderItem);
        });
        return orderMapper.toOrderRes(orderRepository.save(order));
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
        order.setStatus(request.getOrderStatus());
        return orderMapper.toOrderRes(order);
    }

    @Override
    public OrderRes updateOrderInfo(String id, ChangeOrderInfoReq request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setFullName(request.getFullName());
        order.setAddress(request.getAddress());
        order.setPhone(request.getPhone());
        return orderMapper.toOrderRes(order);
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
