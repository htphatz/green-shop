package com.dev.backend.service;

import com.dev.backend.dto.request.ChangeOrderInfoReq;
import com.dev.backend.dto.request.ChangeOrderStatusReq;
import com.dev.backend.dto.request.OrderReq;
import com.dev.backend.dto.response.OrderRes;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.enums.OrderStatus;

public interface OrderService {
    OrderRes createOrder(OrderReq request);
    OrderRes getOrderById(String id) ;
    PageDto<OrderRes> getAllOrders(Integer pageNumber, Integer pageSize);
    OrderRes updateOrderStatus(String id, ChangeOrderStatusReq status);
    OrderRes updateOrderInfo(String id, ChangeOrderInfoReq request);
    PageDto<OrderRes> getMyOrders(Integer pageNumber, Integer pageSize);
    PageDto<OrderRes> searchOrders(OrderStatus orderStatus, String userId, Integer pageNumber, Integer pageSize);
}
