package com.dev.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_EXISTED(409, "User existed", HttpStatus.CONFLICT),
    ROLE_NOT_FOUND(404, "Role not found", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(404, "User not found", HttpStatus.NOT_FOUND),
    PASSWORD_INVALID(400, "Password invalid", HttpStatus.BAD_REQUEST)
    ;

    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(Integer code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
