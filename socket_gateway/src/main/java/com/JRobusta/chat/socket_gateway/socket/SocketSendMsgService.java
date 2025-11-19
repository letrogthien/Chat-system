package com.JRobusta.chat.socket_gateway.socket;

import com.JRobusta.chat.socket_gateway.common.Const;
import com.JRobusta.chat.socket_gateway.dto.AckOffsetDTO;
import com.JRobusta.chat.socket_gateway.dto.SocketMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocketSendMsgService {
    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry simpUserRegistry;




    public void sendMessageToUser(SocketMessageDTO message, String receiverId) {
        messagingTemplate.convertAndSendToUser(receiverId, Const.PRIVATE_MESSAGE_SUFFIX.getValue(), message);
    }

    public void messageStatusNotification(AckOffsetDTO ackOffsetDTO) {
        if (principalExists(ackOffsetDTO.getUserId().toString())){
            messagingTemplate.convertAndSendToUser(ackOffsetDTO.getUserId().toString(), Const.PRIVATE_NOTIFICATION_SUFFIX.getValue(), ackOffsetDTO);
        }
    }

    private boolean principalExists(String userId) {
        return simpUserRegistry.getUser(userId) != null;
    }
    
}
