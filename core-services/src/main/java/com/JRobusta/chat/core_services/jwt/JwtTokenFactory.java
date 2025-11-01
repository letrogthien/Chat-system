package com.JRobusta.chat.core_services.jwt;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.JRobusta.chat.core_services.auth_module.type.TokenType;
import com.JRobusta.chat.core_services.exceptions.CustomException;
import com.JRobusta.chat.core_services.exceptions.ErrorCode;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenFactory {
    private final JwtTokenConfig config;
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;



    public String createToken(String userId, TokenType tokenType)
            throws JOSEException {

        switch (tokenType) {
            case ACCESS_TOKEN, REFRESH_TOKEN:
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


    public boolean validateToken(String token, TokenType tokenType) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        switch (tokenType) {
            case ACCESS_TOKEN:
                JWSVerifier verifier = new RSASSAVerifier(publicKey);
                return signedJWT.verify(verifier);
            case REFRESH_TOKEN:
                JWSVerifier verifierRefresh = new RSASSAVerifier(publicKey);
                return signedJWT.verify(   verifierRefresh);
            default:
                return false;
        }
    }



    public Map<String, Object> extractClaims(String token) {
        try {
            JWT jwt = JWTParser.parse(token);
            return jwt.getJWTClaimsSet().getClaims();
        } catch (Exception var3) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    public String extractClaim(String token, String claim) {
        Map<String, Object> claims = this.extractClaims(token);
        if (claims.containsKey(claim)) {
            return claims.get(claim).toString();
        } else {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

}