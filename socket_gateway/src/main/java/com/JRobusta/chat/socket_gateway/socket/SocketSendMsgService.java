package com.JRobusta.chat.socket_gateway.socket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.JRobusta.chat.socket_gateway.dto.SocketMessageDTO;

@Service
public class SocketSendMsgService {
    private final SimpMessagingTemplate messagingTemplate;

    public SocketSendMsgService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    public void sendMessageToUser(String connectionId, SocketMessageDTO message) {
        String destination = "/topic/user/" + connectionId;
        messagingTemplate.convertAndSend(destination, message);
    }
    
}
