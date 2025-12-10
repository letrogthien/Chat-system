package com.JRobusta.chat.core_services.auth_module.services;

import com.JRobusta.chat.core_services.auth_module.requests.RegisterRequest;
import com.JRobusta.chat.core_services.auth_module.response.RegisterResponse;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenFactory jwtTokenFactory;

  @Transactional
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
          .data(LoginResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build())
          .build();
    } catch (JOSEException e) {
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }


  @Transactional
  public ApiResponse<RegisterResponse> register(RegisterRequest request) {
      if (userRepository.existsByUserName(request.getUsername())) {
        throw new CustomException(ErrorCode.USERNAME_ALREADY_EXISTS);
      }

      User newUser = User.builder().userName(request.getUsername()).email(request.getEmail())
              .displayName(request.getDisplayName())
              .passwordHash(passwordEncoder.encode(request.getPassword())).build();

      userRepository.save(newUser);

      return ApiResponse.<RegisterResponse>builder().data(
          RegisterResponse.builder().message("register success").build())
          .build();
  }

//  public String tokenTest() {
//    try {
//      String accessToken = jwtTokenFactory.createToken("11111111-1111-1111-1111-111111111111",
//          TokenType.ACCESS_TOKEN);
//      return accessToken;
//    } catch (Exception e) {
//      System.out.println("Error creating token: ");
//    }
//    return null;
//  }
//
//  public String tokenTest1() {
//    try {
//      String accessToken = jwtTokenFactory.createToken("22222222-2222-2222-2222-222222222222",
//          TokenType.ACCESS_TOKEN);
//      return accessToken;
//    } catch (Exception e) {
//      System.out.println("Error creating token: ");
//    }
//    return null;
//  }

  private void setCookie(HttpServletResponse response, String name, String value,
      int maxAgeInSeconds, boolean httpOnly, boolean secure, String path) {
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(httpOnly);
    cookie.setSecure(secure);
    cookie.setPath(path);
    cookie.setMaxAge(maxAgeInSeconds);
    response.addCookie(cookie);
  }

  public ApiResponse<String> createGuestSession(String uuid) {
      try {
          String accessToken = jwtTokenFactory.createToken(uuid, TokenType.ACCESS_TOKEN);
          return ApiResponse.<String>builder()
                  .data(accessToken).build();
      } catch (JOSEException e) {
          throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
      }
  }
}
