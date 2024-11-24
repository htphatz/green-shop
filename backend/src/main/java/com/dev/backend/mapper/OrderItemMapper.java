package com.dev.backend.mapper;

import com.dev.backend.dto.request.OrderItemReq;
import com.dev.backend.dto.response.OrderItemRes;
import com.dev.backend.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(target = "product", ignore = true)
    OrderItem toOrderItem(OrderItemReq request);

    @Mapping(target = "productId", ignore = true)
    OrderItemRes toOrderItemRes(OrderItem orderItem);
}
