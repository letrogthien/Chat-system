package com.JRobusta.chat.core_services.auth_module.grpc;

import java.text.ParseException;

import io.grpc.Status;
import org.springframework.grpc.server.service.GrpcService;

import com.JRobusta.chat.core_services.jwt.JwtTokenFactory;
import com.nimbusds.jose.JOSEException;


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

      boolean isValid = jwtTokenFactory.validateToken(accessToken,
          com.JRobusta.chat.core_services.auth_module.type.TokenType.ACCESS_TOKEN);

      if (!isValid) {
        responseObserver.onError(io.grpc.Status.UNAUTHENTICATED
            .withDescription("Invalid access token").asRuntimeException());
        return;
      }

      String userId = jwtTokenFactory.extractClaim(accessToken, "id");

      VerifyResponse response =
          VerifyResponse.newBuilder().setUserId(userId).setWorkspaceId("").setValid(true).build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();

    } catch (ParseException e) {
      responseObserver.onError(Status.UNAUTHENTICATED
          .withDescription("Failed to parse access token").withCause(e).asRuntimeException());
    } catch (JOSEException e) {
      responseObserver.onError(Status.UNAUTHENTICATED.withDescription("Token verification failed")
          .withCause(e).asRuntimeException());
    }
  }


}
