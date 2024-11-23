package com.dev.backend.service;

import com.dev.backend.dto.response.PageDto;
import com.dev.backend.dto.response.UserRes;

public interface UserService {
    UserRes getUserById(String id);
    UserRes getUserByEmail(String email);
    UserRes getMyInfo();
    PageDto<UserRes> getAllUsers(Integer pageNumber, Integer pageSize);
}
