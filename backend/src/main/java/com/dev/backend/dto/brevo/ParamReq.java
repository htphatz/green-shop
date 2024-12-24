package com.dev.backend.dto.brevo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParamReq {
    private String orderId;
}
