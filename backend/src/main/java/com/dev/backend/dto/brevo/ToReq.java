package com.dev.backend.dto.brevo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ToReq {
    private String email;
    private String name;
}
