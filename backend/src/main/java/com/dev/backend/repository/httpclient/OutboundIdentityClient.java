package com.dev.backend.repository.httpclient;

import com.dev.backend.dto.request.ExchangeCodeReq;
import com.dev.backend.dto.response.ExchangeCodeRes;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "outbound-identity", url = "https://oauth2.googleapis.com")
public interface OutboundIdentityClient {
    @PostMapping(value = "token", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ExchangeCodeRes exchangeCode(@QueryMap ExchangeCodeReq request);
}
