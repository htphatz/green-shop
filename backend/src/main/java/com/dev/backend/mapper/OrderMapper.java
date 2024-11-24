package com.dev.backend.mapper;

import com.dev.backend.dto.request.OrderReq;
import com.dev.backend.dto.response.OrderRes;
import com.dev.backend.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toOrder(OrderReq request);

    OrderRes toOrderRes(Order order);
}
