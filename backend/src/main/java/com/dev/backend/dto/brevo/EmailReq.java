package com.dev.backend.dto.brevo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class EmailReq {
    private List<ToReq> to;
    private Integer templateId;
    private ParamReq params;
}
