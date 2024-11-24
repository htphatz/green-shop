package com.dev.backend.service.impl;

import com.dev.backend.dto.response.OrderItemRes;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.entity.OrderItem;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.mapper.OrderItemMapper;
import com.dev.backend.repository.OrderItemRepository;
import com.dev.backend.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderItemRes getById(String id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_ITEM_NOT_FOUND));
        return orderItemMapper.toOrderItemRes(orderItem);
    }

    @Override
    public PageDto<OrderItemRes> findByOrderId(String orderID, Integer pageNumber, Integer pageSize) {
        pageNumber--;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<OrderItem> orderItems = orderItemRepository.findByOrderId(orderID, pageable);
        return PageDto.of(orderItems).map(orderItemMapper::toOrderItemRes);
    }
}
