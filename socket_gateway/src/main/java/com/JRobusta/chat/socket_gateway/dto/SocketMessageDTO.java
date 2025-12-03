package com.JRobusta.chat.socket_gateway.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocketMessageDTO {

    private UUID messageId;

    private UUID conversationId;

    private UUID userId;

    private Long serverSeq;

    private UUID threadRootId;

    private String text;

    private String type = "default";

    private Instant createdAt;

    private Instant updatedAt;

    private Boolean edited = false;
    private Boolean deleted = false;
    
}
