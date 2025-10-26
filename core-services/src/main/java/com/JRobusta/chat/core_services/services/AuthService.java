package com.JRobusta.chat.core_services.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.JRobusta.chat.core_services.entities.User;
import com.JRobusta.chat.core_services.exceptions.CustomException;
import com.JRobusta.chat.core_services.exceptions.ErrorCode;
import com.JRobusta.chat.core_services.repositories.UserRepository;
import com.JRobusta.chat.core_services.requests.LoginRequest;
import com.JRobusta.chat.core_services.response.ApiResponse;
import com.JRobusta.chat.core_services.response.LoginResponse;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ApiResponse<LoginResponse> login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByUserName(request.getUsername())
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!user.isActive()) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }
        if (!this.passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new CustomException(ErrorCode.INVALID_USERNAME_OR_PASSWORD);
        }

        return ApiResponse.<LoginResponse>builder()
                .data(LoginResponse.builder()
                        .accessToken("dummyAccessToken")
                        .refreshToken("dummyRefreshToken")
                        .build())
                .build();
    }

}
