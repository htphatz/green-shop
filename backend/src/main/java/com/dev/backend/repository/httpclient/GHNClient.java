package com.dev.backend.repository.httpclient;

import com.dev.backend.dto.request.DistrictReq;
import com.dev.backend.dto.request.GHNServiceReq;
import com.dev.backend.dto.request.GHNShippingFeeReq;
import com.dev.backend.dto.request.WardReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "ghn-client", url = "https://online-gateway.ghn.vn/shiip/public-api")
public interface GHNClient {
    @GetMapping(value = "master-data/province")
    Object getProvince(@RequestHeader String token);

    @GetMapping(value = "master-data/district")
    Object getDistrict(@RequestHeader String token, @RequestBody DistrictReq request);

    @GetMapping(value = "master-data/ward")
    Object getWard(@RequestHeader String token, @RequestBody WardReq request);

    @GetMapping(value = "v2/shipping-order/available-services")
    Object getService(@RequestHeader String token, @RequestBody GHNServiceReq request);

    @GetMapping(value = "v2/shipping-order/fee")
    Object getShippingFee(@RequestHeader String token, @RequestHeader(value = "shop_id") Integer shopId, @RequestBody GHNShippingFeeReq request);
}
