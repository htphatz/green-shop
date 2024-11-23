package com.dev.backend.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class RegisterReq {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Set<String> roles;
}
