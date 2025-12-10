package com.JRobusta.chat.core_services.auth_module.controllers;

import com.JRobusta.chat.core_services.auth_module.requests.RegisterRequest;
import com.JRobusta.chat.core_services.auth_module.response.RegisterResponse;
import org.springframework.web.bind.annotation.*;

import com.JRobusta.chat.core_services.auth_module.requests.LoginRequest;
import com.JRobusta.chat.core_services.auth_module.response.ApiResponse;
import com.JRobusta.chat.core_services.auth_module.response.LoginResponse;
import com.JRobusta.chat.core_services.auth_module.services.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthService authService;


  @PostMapping("/login")
  public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request,
      HttpServletResponse response) {
    return authService.login(request, response);
  }

//  @GetMapping("/jwt")
//  public String tokenTest() {
//    return authService.tokenTest();
//  }
//
//  @GetMapping("/jwt/1")
//  public String tokenTest1() {
//    return authService.tokenTest1();
//  }

  @PostMapping("/register")
  public ApiResponse<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
    return authService.register(registerRequest);
  }

  @PostMapping("/guest/session")
  public ApiResponse<String> createGuestSession(@RequestParam String uuid) {
    return authService.createGuestSession(uuid);
  }

}
