package com.JRobusta.chat.core_services.securities;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.JRobusta.chat.core_services.exceptions.CustomException;
import com.JRobusta.chat.core_services.exceptions.ErrorCode;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
              Throwable cause = authException.getCause();
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        if (cause instanceof JwtException jwtEx) {
            String message = jwtEx.getMessage();
            if (message.contains("expired")) {
                errorCode = ErrorCode.TOKEN_EXPIRED;
            } else if (message.contains("signature")) {
                errorCode = ErrorCode.INVALID_SIGNATURE;
            } else {
                errorCode = ErrorCode.INVALID_TOKEN;
            }
        }

        CustomException exception = new CustomException(errorCode);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String responseBody = exception.toString();
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(responseBody.getBytes());
            outputStream.flush();
        }
    }
    
}
