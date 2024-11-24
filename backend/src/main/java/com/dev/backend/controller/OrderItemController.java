package com.dev.backend.controller;

import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.dto.response.OrderItemRes;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("order-items")
@RequiredArgsConstructor
public class OrderItemController {
    private final OrderItemService orderItemService;

    @GetMapping("{id}")
    public APIResponse<OrderItemRes> getById(@PathVariable("id") String id) {
        OrderItemRes result = orderItemService.getById(id);
        return APIResponse.<OrderItemRes>builder().result(result).build();
    }

    @GetMapping("order/{id}")
    public APIResponse<PageDto<OrderItemRes>> getByOrderId(
            @PathVariable("id") String orderId,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        PageDto<OrderItemRes> result = orderItemService.findByOrderId(orderId, pageNumber, pageSize);
        return APIResponse.<PageDto<OrderItemRes>>builder().result(result).build();
    }
}
