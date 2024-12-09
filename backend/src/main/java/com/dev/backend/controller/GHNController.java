package com.dev.backend.controller;

import com.dev.backend.dto.request.DistrictReq;
import com.dev.backend.dto.request.GHNServiceReq;
import com.dev.backend.dto.request.GHNShippingFeeReq;
import com.dev.backend.dto.request.WardReq;
import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.repository.httpclient.GHNClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ghn")
@RequiredArgsConstructor
public class GHNController {
    private final GHNClient ghnClient;

    @Value(value = "${ghn.token}")
    private String token;

    @Value(value = "${ghn.shop-id}")
    private Integer shopId;

    @GetMapping("province")
    public APIResponse<Object> getProvince() {
        Object result = ghnClient.getProvince(token);
        return APIResponse.<Object>builder().result(result).build();
    }

    @GetMapping("district")
    public APIResponse<Object> getDistrict(@Valid @RequestBody DistrictReq request) {
        Object result = ghnClient.getDistrict(token, request);
        return APIResponse.<Object>builder().result(result).build();
    }

    @GetMapping("ward")
    public APIResponse<Object> getWard(@Valid @RequestBody WardReq request) {
        Object result = ghnClient.getWard(token, request);
        return APIResponse.<Object>builder().result(result).build();
    }

    @GetMapping("service")
    public APIResponse<Object> getService(@Valid @RequestBody GHNServiceReq request) {
        request.setShopId(shopId);
        Object result = ghnClient.getService(token, request);
        return APIResponse.<Object>builder().result(result).build();
    }

    @GetMapping("fee")
    public APIResponse<Object> getShippingFee(@Valid @RequestBody GHNShippingFeeReq request) {
        Object result = ghnClient.getShippingFee(token, shopId, request);
        return APIResponse.<Object>builder().result(result).build();
    }
}
