package com.dev.backend.service;

import com.dev.backend.dto.request.OrderItemReq;
import com.dev.backend.dto.response.OrderItemRes;
import com.dev.backend.dto.response.PageDto;

public interface OrderItemService {
    OrderItemRes createOrderItem(OrderItemReq request);
    OrderItemRes getById(String id);
    void deleteById(String id);
    PageDto<OrderItemRes> findByOrderId(String orderID, Integer pageNumber, Integer pageSize);
}
