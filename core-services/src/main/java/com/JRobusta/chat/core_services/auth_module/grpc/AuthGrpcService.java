package com.JRobusta.chat.core_services.auth_module.grpc;

import java.text.ParseException;

import org.springframework.grpc.server.service.GrpcService;

import com.JRobusta.chat.core_services.jwt.JwtTokenFactory;
import com.nimbusds.jose.JOSEException;

import auth.v1.Auth.HelloRequest;
import auth.v1.Auth.HelloResponse;
import auth.v1.Auth.VerifyRequest;
import auth.v1.Auth.VerifyResponse;
import auth.v1.AuthServiceGrpc.AuthServiceImplBase;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@GrpcService
@RequiredArgsConstructor
public class AuthGrpcService extends AuthServiceImplBase {
    private final JwtTokenFactory jwtTokenFactory;

    @Override
    public void verifyAccessToken(VerifyRequest request,
            StreamObserver<VerifyResponse> responseObserver) {
        String accessToken = request.getAccessToken();
        try {
            System.out.println("Verifying access token: " + accessToken);

            boolean isValid = jwtTokenFactory.validateToken(
                    accessToken,
                    com.JRobusta.chat.core_services.auth_module.type.TokenType.ACCESS_TOKEN);

            if (!isValid) {
                responseObserver.onError(
                        io.grpc.Status.UNAUTHENTICATED
                                .withDescription("Invalid access token")
                                .asRuntimeException());
                return;
            }

            // ✅ Nếu token hợp lệ
            String userId = jwtTokenFactory.extractClaim(accessToken, "id");
            System.out.println("Access token is valid for user id: " + userId);

            VerifyResponse response = VerifyResponse.newBuilder()
                    .setUserId(userId)
                    .setWorkspaceId("") // bạn có thể set nếu cần
                    .setValid(true)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (ParseException e) {
            responseObserver.onError(
                    io.grpc.Status.INVALID_ARGUMENT
                            .withDescription("Failed to parse access token")
                            .withCause(e)
                            .asRuntimeException());
        } catch (JOSEException e) {
            responseObserver.onError(
                    io.grpc.Status.INTERNAL
                            .withDescription("Failed to process access token")
                            .withCause(e)
                            .asRuntimeException());
        }
    }

    @Override
    public void hello(HelloRequest request,
            StreamObserver<HelloResponse> responseObserver) {
        System.out.println("Received hello request: " + request.getName());
        HelloResponse response = HelloResponse.newBuilder()
                .setMessage("Hello from AuthGrpcService!")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
