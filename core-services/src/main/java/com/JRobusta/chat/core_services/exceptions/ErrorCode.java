package com.JRobusta.chat.core_services.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNAUTHORIZED("E001", "Unauthorized", HttpStatus.UNAUTHORIZED), 
    TOKEN_EXPIRED("E002", "Token expired", HttpStatus.UNAUTHORIZED),
    INVALID_SIGNATURE("E003", "Invalid token signature", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("E004", "Invalid token", HttpStatus.UNAUTHORIZED), 
    USER_NOT_FOUND("E005", "User not found", HttpStatus.NOT_FOUND), 
    INVALID_USERNAME_OR_PASSWORD("E006", "Invalid username or password", HttpStatus.UNAUTHORIZED), 
    ACCOUNT_NOT_ACTIVE("E007", "Account not active", HttpStatus.UNAUTHORIZED), 
    INTERNAL_SERVER_ERROR("E500", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

}