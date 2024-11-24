package com.dev.backend.service;

import com.dev.backend.dto.response.OrderItemRes;
import com.dev.backend.dto.response.PageDto;

public interface OrderItemService {
    OrderItemRes getById(String id);
    PageDto<OrderItemRes> findByOrderId(String orderID, Integer pageNumber, Integer pageSize);
}
