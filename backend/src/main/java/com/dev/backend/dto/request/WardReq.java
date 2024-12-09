package com.dev.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WardReq {
    @NotNull(message = "District's ID is required")
    @JsonProperty("district_id")
    private Integer districtId;
}
