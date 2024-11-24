package com.dev.backend.service.impl;

import com.dev.backend.dto.request.OrderItemReq;
import com.dev.backend.dto.response.OrderItemRes;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.entity.OrderItem;
import com.dev.backend.mapper.OrderItemMapper;
import com.dev.backend.repository.OrderItemRepository;
import com.dev.backend.repository.OrderRepository;
import com.dev.backend.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderItemRes createOrderItem(OrderItemReq request) {
        OrderItem orderItem = orderItemMapper.toOrderItem(request);
        return null;
    }

    @Override
    public OrderItemRes getById(String id) {
        return null;
    }

    @Override
    public void deleteById(String id) {

    }

    @Override
    public PageDto<OrderItemRes> findByOrderId(String orderID, Integer pageNumber, Integer pageSize) {
        return null;
    }
}
