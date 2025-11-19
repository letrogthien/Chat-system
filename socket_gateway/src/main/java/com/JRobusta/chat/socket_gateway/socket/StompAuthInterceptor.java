package com.JRobusta.chat.socket_gateway.socket;


import auth.v1.Auth;
import com.JRobusta.chat.socket_gateway.common.Const;
import com.JRobusta.chat.socket_gateway.grpc.client.AuthGrpcClient;
import com.JRobusta.chat.socket_gateway.grpc.client.ConnectionManagerClient;
import com.JRobusta.chat.socket_gateway.redis.RedisService;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.Timestamps;
import connection.v1.ConnectionManagerOuterClass;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {
    private final AuthGrpcClient authGrpcClient;
    private final RedisService redisService;
    private final ConnectionManagerClient connectionManagerClient;


    @Value("${gateway.id}")
    private String gatewayNodeId;


    @Override
    public Message<?> preSend(Message<?> message, org.springframework.messaging.MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();
        String userId = Objects.toString(
                Objects.requireNonNull(accessor.getSessionAttributes()).get("userId"),
                ""
        );
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {


            Instant now = Instant.now();
            Timestamp ts = Timestamps.fromMillis(now.toEpochMilli());
            ConnectionManagerOuterClass.Connection connection = ConnectionManagerOuterClass.Connection
                    .newBuilder()
                    .setConnectionId(sessionId)
                    .setUserId(userId)
                    .setWorkspaceId("sample")
                    .setGatewayNodeId(gatewayNodeId)
                    .setConnectedAt(ts)
                    .setLastPingAt(ts)
                    .setDeviceType("web")
                    .setIpAddress(Objects.toString(
                            Objects.requireNonNull(accessor.getSessionAttributes()).get(Const.CLIENT_IP_ATTRIBUTE.getValue()),
                            ""
                    ))
                    .setSessionState(ConnectionManagerOuterClass.SessionState.CONNECTED)
                    .build();

            boolean a = connectionManagerClient.saveConnection(connection, userId);
            if ( !a) {
                return null;
            }
            // Store the verification result in Redis
            redisService.saveConnectionVerified(sessionId, true);
            return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
        }
        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {

            if (!connectionManagerClient.removeConnection(sessionId)) {

                System.out.println("Failed to remove connection for sessionId: " + sessionId);
                return null;
            }
            redisService.deleteConnectionVerified(sessionId);

            return message;
        }
        return message;
    }

}
