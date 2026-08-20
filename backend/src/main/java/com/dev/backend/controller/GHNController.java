package com.dev.backend.controller;

import com.dev.backend.dto.request.DistrictReq;
import com.dev.backend.dto.request.GHNServiceReq;
import com.dev.backend.dto.request.GHNShippingFeeReq;
import com.dev.backend.dto.request.WardReq;
import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.repository.httpclient.GHNClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("ghn")
@RequiredArgsConstructor
@Tag(name = "GHN APIs")
public class GHNController {
    private final GHNClient ghnClient;

    @Value(value = "${ghn.token}")
    private String token;

    @Value(value = "${ghn.shop-id}")
    private Integer shopId;

    @GetMapping("province")
    @Operation(summary = "Get all provinces")
    public APIResponse<Object> getProvince() {
        Object result = ghnClient.getProvince(token);
        return APIResponse.<Object>builder().result(result).build();
    }

    @GetMapping("district")
    @Operation(summary = "Get all districts by province's id")
    public APIResponse<Object> getDistrict(@Valid @RequestBody DistrictReq request) {
        Object result = ghnClient.getDistrict(token, request);
        return APIResponse.<Object>builder().result(result).build();
    }

    @GetMapping("ward")
    @Operation(summary = "Get all wards by district's id")
    public APIResponse<Object> getWard(@Valid @RequestBody WardReq request) {
        Object result = ghnClient.getWard(token, request);
        return APIResponse.<Object>builder().result(result).build();
    }

    @GetMapping("service")
    @Operation(summary = "Get service")
    public APIResponse<Object> getService(@Valid @RequestBody GHNServiceReq request) {
        request.setShopId(shopId);
        Object result = ghnClient.getService(token, request);
        return APIResponse.<Object>builder().result(result).build();
    }

    @GetMapping("fee")
    @Operation(summary = "Get shipping fee")
    @CircuitBreaker(name = "ghnService", fallbackMethod = "getShippingFeeFallback")
    public APIResponse<Object> getShippingFee(@Valid @RequestBody GHNShippingFeeReq request) {
        Object result = ghnClient.getShippingFee(token, shopId, request);
        return APIResponse.<Object>builder().result(result).build();
    }

    public APIResponse<Object> getShippingFeeFallback(Throwable throwable) {
        log.error("GHN Service fallback triggered. Reason: {}", throwable.getMessage());
        return APIResponse.<Object>builder()
                .code(200)
                .message("GHN service is temporarily unavailable. Applied default shipping fee of 30,000 VND.")
                .result(30000)
                .build();
    }
}
