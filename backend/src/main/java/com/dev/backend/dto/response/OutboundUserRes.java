package com.dev.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OutboundUserRes {
    String id;
    String email;

    @JsonProperty("verified_email")
    boolean verifiedEmail;
    String name;

    @JsonProperty("given_name")
    String givenName;

    @JsonProperty("family_name")
    String familyName;

    String picture;
    String locale;
}
