package com.dev.backend.controller;

import com.dev.backend.dto.request.PreviewOrderItemReq;
import com.dev.backend.dto.request.PreviewOrderReq;
import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.dto.response.PreviewOrderItemRes;
import com.dev.backend.dto.response.PreviewOrderRes;
import com.dev.backend.service.PreviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("preview")
@RequiredArgsConstructor
public class PreviewController {
    private final PreviewService previewService;

    @PostMapping("order-item")
    public APIResponse<PreviewOrderItemRes> previewOrderItem(@Valid @RequestBody PreviewOrderItemReq request) {
        PreviewOrderItemRes result = previewService.previewOrderItem(request);
        return APIResponse.<PreviewOrderItemRes>builder().result(result).build();
    }

    @PostMapping("order")
    public APIResponse<PreviewOrderRes> previewOrder(@Valid @RequestBody PreviewOrderReq request) {
        PreviewOrderRes result = previewService.previewOrder(request);
        return APIResponse.<PreviewOrderRes>builder().result(result).build();
    }
}
