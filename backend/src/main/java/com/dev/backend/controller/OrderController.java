package com.dev.backend.controller;

import com.dev.backend.dto.request.ChangeOrderInfoReq;
import com.dev.backend.dto.request.ChangeOrderStatusReq;
import com.dev.backend.dto.request.OrderReq;
import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.dto.response.OrderRes;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.enums.OrderStatus;
import com.dev.backend.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
@Tag(name = "Order APIs")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create order")
    public APIResponse<OrderRes> createOrder(@Valid @RequestBody OrderReq request) throws JsonProcessingException {
        OrderRes result = orderService.createOrder(request);
        return APIResponse.<OrderRes>builder().result(result).build();
    }

    @GetMapping
    @Operation(summary = "Get all orders")
    public APIResponse<PageDto<OrderRes>> getAllOrders(
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        PageDto<OrderRes> result = orderService.getAllOrders(pageNumber, pageSize);
        return APIResponse.<PageDto<OrderRes>>builder().result(result).build();
    }

    @GetMapping("{id}")
    @Operation(summary = "Get order by id")
    public APIResponse<OrderRes> getOrderById(@PathVariable("id") String id) {
        OrderRes result = orderService.getOrderById(id);
        return APIResponse.<OrderRes>builder().result(result).build();
    }

    @PutMapping("status/{id}")
    @Operation(summary = "Update order's status")
    public APIResponse<OrderRes> updateOrderStatus(@Valid @PathVariable("id") String id, @Valid @RequestBody ChangeOrderStatusReq request) {
        OrderRes result = orderService.updateOrderStatus(id, request);
        return APIResponse.<OrderRes>builder().result(result).build();
    }

    @PutMapping("info/{id}")
    @Operation(summary = "Update order's info")
    public APIResponse<OrderRes> updateOrderInfo(@PathVariable("id") String id, @Valid @RequestBody ChangeOrderInfoReq request) {
        OrderRes result = orderService.updateOrderInfo(id, request);
        return APIResponse.<OrderRes>builder().result(result).build();
    }

    @GetMapping("my-orders")
    @Operation(summary = "Get my order")
    public APIResponse<PageDto<OrderRes>> getMyOrders(
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        PageDto<OrderRes> result = orderService.getMyOrders(pageNumber, pageSize);
        return APIResponse.<PageDto<OrderRes>>builder().result(result).build();
    }

    @GetMapping("search")
    @Operation(summary = "Search orders")
    public APIResponse<PageDto<OrderRes>> searchOrders(
            @RequestParam(name = "status", required = false) OrderStatus status,
            @RequestParam(name = "userId", required = false) String userId,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        PageDto<OrderRes> result = orderService.searchOrders(status, userId, pageNumber, pageSize);
        return APIResponse.<PageDto<OrderRes>>builder().result(result).build();
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete order")
    public APIResponse<Void> deleteOrder(@PathVariable("id") String id) {
        orderService.deleteOrder(id);
        return APIResponse.<Void>builder().build();
    }
}
