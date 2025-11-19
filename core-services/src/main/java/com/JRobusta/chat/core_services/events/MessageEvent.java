package com.JRobusta.chat.core_services.events;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;


@Data
@Builder
public class MessageEvent {

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
