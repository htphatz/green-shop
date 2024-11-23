package com.dev.backend.mapper;

import com.dev.backend.dto.request.RegisterReq;
import com.dev.backend.dto.response.UserRes;
import com.dev.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    User toUser(RegisterReq request);

    UserRes toUserRes(User user);
}
