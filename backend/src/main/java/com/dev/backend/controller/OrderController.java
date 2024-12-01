package com.dev.backend.controller;

import com.dev.backend.dto.request.ChangeOrderInfoReq;
import com.dev.backend.dto.request.ChangeOrderStatusReq;
import com.dev.backend.dto.request.OrderReq;
import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.dto.response.OrderRes;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.enums.OrderStatus;
import com.dev.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public APIResponse<OrderRes> createOrder(@Valid @RequestBody OrderReq request) {
        OrderRes result = orderService.createOrder(request);
        return APIResponse.<OrderRes>builder().result(result).build();
    }

    @GetMapping
    public APIResponse<PageDto<OrderRes>> getAllOrders(
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        PageDto<OrderRes> result = orderService.getAllOrders(pageNumber, pageSize);
        return APIResponse.<PageDto<OrderRes>>builder().result(result).build();
    }

    @GetMapping("{id}")
    public APIResponse<OrderRes> getOrderById(@PathVariable("id") String id) {
        OrderRes result = orderService.getOrderById(id);
        return APIResponse.<OrderRes>builder().result(result).build();
    }

    @PutMapping("status/{id}")
    public APIResponse<OrderRes> updateOrderStatus(@Valid @PathVariable("id") String id, @Valid @RequestBody ChangeOrderStatusReq request) {
        OrderRes result = orderService.updateOrderStatus(id, request);
        return APIResponse.<OrderRes>builder().result(result).build();
    }

    @PutMapping("info/{id}")
    public APIResponse<OrderRes> updateOrderInfoCategory(@PathVariable("id") String id, @Valid @RequestBody ChangeOrderInfoReq request) {
        OrderRes result = orderService.updateOrderInfo(id, request);
        return APIResponse.<OrderRes>builder().result(result).build();
    }

    @GetMapping("my-orders")
    public APIResponse<PageDto<OrderRes>> getMyOrders(
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        PageDto<OrderRes> result = orderService.getMyOrders(pageNumber, pageSize);
        return APIResponse.<PageDto<OrderRes>>builder().result(result).build();
    }

    @GetMapping("search")
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
    public APIResponse<Void> deleteOrder(@PathVariable("id") String id) {
        orderService.deleteOrder(id);
        return APIResponse.<Void>builder().build();
    }
}
