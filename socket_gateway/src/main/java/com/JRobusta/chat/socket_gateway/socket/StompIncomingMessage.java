package com.JRobusta.chat.socket_gateway.socket;

import com.JRobusta.chat.socket_gateway.dto.SocketMessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;


@Component
@RequiredArgsConstructor
public class StompIncomingMessage implements ChannelInterceptor {
    private final ObjectMapper objectMapper;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() == StompCommand.SEND) {
            Principal principal = accessor.getUser();
            if (principal == null) {
                throw new MessagingException("No principal found (unauthenticated websocket)");
            }
            String userId = principal.getName();
            String json = new String((byte[]) message.getPayload());
            SocketMessageDTO dto;
            try {
                dto = objectMapper.readValue(json, SocketMessageDTO.class);
            } catch (Exception e) {
                throw new MessagingException("Failed to parse incoming message JSON", e);
            }
            if (!dto.getUserId().toString().equals(userId)) {
                throw new MessagingException("UserId mismatch — spoofed sender rejected");
            }

        }
        return message;
    }
}
