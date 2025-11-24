package com.JRobusta.chat.socket_gateway.socket;

import auth.v1.Auth;
import com.JRobusta.chat.socket_gateway.grpc.client.AuthGrpcClient;
import com.JRobusta.chat.socket_gateway.grpc.client.ConnectionManagerClient;
import com.JRobusta.chat.socket_gateway.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;


@RequiredArgsConstructor
@Component
public class CusHandshakeInterceptor implements HandshakeInterceptor  {
    private final AuthGrpcClient authGrpcClient;


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        String token = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst("token");
        if (token == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        Auth.VerifyResponse verified = authGrpcClient.verifyAccessToken(token);
        System.out.println("valid? :" + verified.getValid());
        if (verified == null || !verified.getValid()) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        attributes.put("userId", verified.getUserId());
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
