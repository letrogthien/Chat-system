package com.JRobusta.chat.core_services.auth_module.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.JRobusta.chat.core_services.auth_module.requests.LoginRequest;
import com.JRobusta.chat.core_services.auth_module.response.ApiResponse;
import com.JRobusta.chat.core_services.auth_module.response.LoginResponse;
import com.JRobusta.chat.core_services.auth_module.services.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;



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

  @GetMapping("/jwt")
  public String tokenTest() {
    return authService.tokenTest();
  }

  @GetMapping("/jwt/1")
  public String tokenTest1() {
    return authService.tokenTest1();
  }
}
