package com.dev.backend.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangeOrderInfoReq {
    private String fullName;
    private String phone;
    private String address;
}
