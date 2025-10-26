package com.JRobusta.chat.core_services.securities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        List<GrantedAuthority> roles = this.extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, roles);

    }

    private List<GrantedAuthority> extractAuthorities(Jwt jwt) {
        return this.extractAuthoritiesFromToken(jwt);
    }

    private List<GrantedAuthority> extractAuthoritiesFromToken(Jwt jwt) {
        List<GrantedAuthority> roles = new ArrayList<>();
        if (jwt.getClaim("roles") != null) {
            List<String> roleList = jwt.getClaim("roles");
            roles = roleList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        }

        return roles;
    }

}