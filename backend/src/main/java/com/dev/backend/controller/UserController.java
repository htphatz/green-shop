package com.dev.backend.controller;

import com.dev.backend.dto.response.APIResponse;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.dto.response.UserRes;
import com.dev.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("me")
    public APIResponse<UserRes> getCurrentInformation() {
        UserRes result = userService.getMyInfo();
        return APIResponse.<UserRes>builder().result(result).build();
    }

    @GetMapping
    public APIResponse<PageDto<UserRes>> getAllUsers(
            @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        PageDto<UserRes> result = userService.getAllUsers(pageNumber, pageSize);
        return APIResponse.<PageDto<UserRes>>builder().result(result).build();
    }

    @GetMapping("{id}")
    public APIResponse<UserRes> getUserById(@PathVariable("id") String id) {
        UserRes result = userService.getUserById(id);
        return APIResponse.<UserRes>builder().result(result).build();
    }

//    @GetMapping("{email}")
//    public APIResponse<UserRes> getUserByEmail(@PathVariable("email") String email) {
//        UserRes result = userService.getUserByEmail(email);
//        return APIResponse.<UserRes>builder().result(result).build();
//    }
}
