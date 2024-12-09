package com.dev.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GHNShippingFeeReq {
    @NotNull(message = "GHN Service's ID is required")
    @JsonProperty("service_id")
    private Integer serviceId;

    @NotBlank(message = "To ward code is required")
    @JsonProperty("to_ward_code")
    private String toWardCode;

    @NotNull(message = "To district ID is required")
    @JsonProperty("to_district_id")
    private Integer toDistrictId;

    @NotNull(message = "Weight is required")
    private Integer weight;
}
