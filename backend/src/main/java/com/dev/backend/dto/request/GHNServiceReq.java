package com.dev.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GHNServiceReq {
    @NotNull(message = "Shop's ID is required")
    @JsonProperty("shop_id")
    private Integer shopId;

    @NotNull(message = "From district is required")
    @JsonProperty("from_district")
    private Integer fromDistrict;

    @NotNull(message = "To district is required")
    @JsonProperty("to_district")
    private Integer toDistrict;
}
