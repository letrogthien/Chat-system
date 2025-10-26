package com.JRobusta.chat.core_services.securities;

import java.security.interfaces.RSAPublicKey;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CusJwtDecoder implements JwtDecoder {
    private final RSAPublicKey rsaPublicKey;

    @Override
    public Jwt decode(String token) throws JwtException {
        return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build().decode(token);
    }

}
