package com.JRobusta.chat.core_services.securities;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class GetTokenResolver implements BearerTokenResolver {
  @Override
  public String resolve(HttpServletRequest request) {
    String uri = request.getRequestURI();
    String[] publicEndpoints = {

    };
    for (String endpoint : publicEndpoints) {
      if (uri.startsWith(endpoint)) {
        return null;
      }
    }
    String authorizationHeader = request.getHeader("Authorization");
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      return authorizationHeader.substring(7);
    }
    if (request.getCookies() != null) {
      for (var cookie : request.getCookies()) {
        if ("access_token".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
}
