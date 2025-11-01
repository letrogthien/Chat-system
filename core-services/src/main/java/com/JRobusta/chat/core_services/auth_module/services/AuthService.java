package com.JRobusta.chat.core_services.auth_module.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.JRobusta.chat.core_services.auth_module.entities.User;
import com.JRobusta.chat.core_services.auth_module.repositories.UserRepository;
import com.JRobusta.chat.core_services.auth_module.requests.LoginRequest;
import com.JRobusta.chat.core_services.auth_module.response.ApiResponse;
import com.JRobusta.chat.core_services.auth_module.response.LoginResponse;
import com.JRobusta.chat.core_services.auth_module.type.TokenType;
import com.JRobusta.chat.core_services.exceptions.CustomException;
import com.JRobusta.chat.core_services.exceptions.ErrorCode;
import com.JRobusta.chat.core_services.jwt.JwtTokenFactory;
import com.nimbusds.jose.JOSEException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenFactory jwtTokenFactory;

    public ApiResponse<LoginResponse> login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByUserName(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!user.isActive()) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }
        if (!this.passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new CustomException(ErrorCode.INVALID_USERNAME_OR_PASSWORD);
        }

        try {
            String accessToken = jwtTokenFactory.createToken(user.getId(), TokenType.ACCESS_TOKEN);
            setCookie(response, "accessToken", accessToken, 3600, false, false, "/");
            String refreshToken = jwtTokenFactory.createToken(user.getId(), TokenType.REFRESH_TOKEN);
            setCookie(response, "refreshToken", refreshToken, 604800, false, false, "/");

            return ApiResponse.<LoginResponse>builder()
                    .data(LoginResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build())
                    .build();
        } catch (JOSEException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

    public String tokenTest() {
        try {
            String accessToken = jwtTokenFactory.createToken("userid9999", TokenType.ACCESS_TOKEN);
            return accessToken;
        } catch (Exception e) {
            System.out.println("Error creating token: ");
        }
        return null;
    }

    private void setCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds,
            boolean httpOnly, boolean secure, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        cookie.setPath(path);
        cookie.setMaxAge(maxAgeInSeconds);
        response.addCookie(cookie);
    }

}
