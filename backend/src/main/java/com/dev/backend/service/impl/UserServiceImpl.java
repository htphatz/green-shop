package com.dev.backend.service.impl;

import com.dev.backend.dto.request.ChangePasswordReq;
import com.dev.backend.dto.request.UpdateUserInfoReq;
import com.dev.backend.dto.response.PageDto;
import com.dev.backend.dto.response.UserRes;
import com.dev.backend.entity.User;
import com.dev.backend.exception.AppException;
import com.dev.backend.exception.ErrorCode;
import com.dev.backend.mapper.UserMapper;
import com.dev.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements com.dev.backend.service.UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRes getUserById(String id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserRes(existingUser);
    }

    @Override
    public UserRes getUserByEmail(String email) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserRes(existingUser);
    }

    @Override
    public UserRes getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserRes(existingUser);
    }

    @Override
    public PageDto<UserRes> getAllUsers(Integer pageNumber, Integer pageSize) {
        pageNumber--;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> users = userRepository.findAll(pageable);
        return PageDto.of(users.map(userMapper::toUserRes));
    }

    @Override
    public UserRes updateMyInfo(UpdateUserInfoReq request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toUserRes(updatedUser);
    }

    @Override
    public void changePassword(ChangePasswordReq request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Validate current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), existingUser.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }

        // Validate new password and confirm password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_CONFIRM_INVALID);
        }

        // Update password
        existingUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(existingUser);
    }
}
