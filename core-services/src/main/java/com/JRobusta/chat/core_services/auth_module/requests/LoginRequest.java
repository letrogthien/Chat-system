package com.JRobusta.chat.core_services.auth_module.requests;

import lombok.Getter;

@Getter
public class LoginRequest {
  private String username;
  private String password;
}
