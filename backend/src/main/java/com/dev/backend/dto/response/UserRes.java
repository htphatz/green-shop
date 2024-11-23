package com.dev.backend.dto.response;

import com.dev.backend.entity.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class UserRes {
    private String firstName;
    private String lastName;
    private String email;
    private Set<Role> roles;
}
