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
public class AckOffsetDTO {

    private UUID conversationId;

    private UUID userId;

    private Long ackedSeq;

    private String status;

    private String clientMsgId;

    private Instant ackedAt;
}
