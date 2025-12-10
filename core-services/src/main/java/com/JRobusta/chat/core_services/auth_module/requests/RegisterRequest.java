package com.JRobusta.chat.core_services.auth_module.requests;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String displayName;
}
