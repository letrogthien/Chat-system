package com.JRobusta.chat.socket_gateway.dto;


import lombok.*;
import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocketMessageDTO {

    /**
     * Kiểu event (giống Slack): message, typing, reaction_added, user_joined, user_left, etc.
     */
    private MessageType type;

    /**
     * ID workspace hiện tại
     */
    private String workspaceId;

    /**
     * ID cuộc trò chuyện (channel hoặc DM)
     */
    private String conversationId;

    /**
     * ID người gửi (server có thể điền vào khi xác thực xong)
     */
    private String senderId;

    /**
     * Nội dung text của message (chỉ dùng khi type = "message")
     */
    private String text;

    /**
     * Thời điểm tạo message (client có thể gửi hoặc server sẽ set)
     */
    private Instant timestamp;

    /**
     * Dữ liệu phụ kèm theo (reaction, typing state, file info...)
     */
    private Map<String, Object> payload;

    /**
     * ID request (client tự sinh để tracking ACK)
     */
    private String clientMsgId;
}
