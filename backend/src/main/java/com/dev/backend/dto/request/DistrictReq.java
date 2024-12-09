package com.dev.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DistrictReq {
    @NotNull(message = "Province's ID is required")
    @JsonProperty("province_id")
    private Integer provinceId;
}
