package com.JRobusta.chat.socket_gateway.socket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SocketController {

    private final SocketSendMsgService socketSendMsgService;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public void sendMessage(String message, SimpMessageHeaderAccessor headerAccessor) {
        String connectionId = headerAccessor.getSessionId();
        socketSendMsgService.sendMessageToUser(connectionId, null);
    }

}
