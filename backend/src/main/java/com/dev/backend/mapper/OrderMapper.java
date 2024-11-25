package com.dev.backend.mapper;

import com.dev.backend.dto.request.OrderReq;
import com.dev.backend.dto.response.OrderRes;
import com.dev.backend.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "orderItems", ignore = true)
    Order toOrder(OrderReq request);

    OrderRes toOrderRes(Order order);
}
