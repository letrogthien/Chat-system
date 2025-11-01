package com.JRobusta.chat.core_services.auth_module.response;

import java.time.Instant;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder.Default;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    @Default
    private HttpStatus status = HttpStatus.OK;
    private T data;
    private String message;
    @Default
    private Instant timestamp = Instant.now();
}

