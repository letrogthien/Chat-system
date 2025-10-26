package com.JRobusta.chat.core_services.jwt;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.JRobusta.chat.core_services.type.TokenType;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenFactory {
    private final JwtTokenConfig config;
    private final RSAPrivateKey privateKey;

    public String createToken(String userId, TokenType tokenType) throws JOSEException {
        return this.createToken(new HashMap<>(), userId, tokenType);
    }

    public String createToken(Map<String, Object> extraClaims, String userId, TokenType tokenType)
            throws JOSEException {

        extraClaims.put("id", userId);
        extraClaims.put("jti", UUID.randomUUID().toString());

        if (tokenType == TokenType.ACCESS_TOKEN) {

        }
        switch (tokenType) {
            case ACCESS_TOKEN:
                JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                        .type(com.nimbusds.jose.JOSEObjectType.JWT)
                        .keyID("auth-key-001").build();

                JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                        .claim("id", userId)
                        .jwtID(UUID.randomUUID().toString())
                        .issueTime(java.util.Date.from(Instant.now()))
                        .expirationTime(java.util.Date
                                .from(Instant.now().plusMillis(this.config.getTokenConfig(tokenType).getExpiration())))
                        .build();
                SignedJWT signedJWT = new SignedJWT(header, claimsSet);
                signedJWT.sign(new RSASSASigner(privateKey));
                return signedJWT.serialize();

            default:
                return null;
        }
    }

}