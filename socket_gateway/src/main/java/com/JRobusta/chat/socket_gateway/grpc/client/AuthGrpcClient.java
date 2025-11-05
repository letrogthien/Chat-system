package com.JRobusta.chat.socket_gateway.grpc.client;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import auth.v1.Auth.VerifyRequest;
import auth.v1.Auth.VerifyResponse;
import auth.v1.AuthServiceGrpc.AuthServiceBlockingStub;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthGrpcClient {

    private final AuthServiceBlockingStub authServiceBlockingStub;

    public VerifyResponse verifyAccessToken(String token) {
        VerifyRequest request = VerifyRequest.newBuilder()
                .setAccessToken(token)
                .build();

        try {
            return authServiceBlockingStub.withDeadlineAfter(5, TimeUnit.SECONDS)
                    .verifyAccessToken(request);
        } catch (StatusRuntimeException e) {
            Status.Code code = e.getStatus().getCode();
            if (code == Status.Code.UNAUTHENTICATED) {
                // unauthenticated fallback: treat as invalid token
                return VerifyResponse.newBuilder()
                        .setValid(false)
                        .setUserId("")
                        .setWorkspaceId("")
                        .build();
            }
            throw e;
        }
    }



}
