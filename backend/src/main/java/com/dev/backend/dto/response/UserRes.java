package com.dev.backend.dto.response;

import com.dev.backend.entity.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class UserRes {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean active;
    private Set<Role> roles;
}
