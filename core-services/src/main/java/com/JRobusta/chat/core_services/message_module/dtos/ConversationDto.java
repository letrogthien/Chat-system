package com.JRobusta.chat.core_services.message_module.dtos;

import java.time.Instant;
import java.util.UUID;

import com.JRobusta.chat.core_services.message_module.entities.Conversation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationDto {
    private UUID conversationId;

    private Conversation.ConversationType type;

    private String name;

    private String topic;

    private Boolean isPrivate = false;

    private UUID createdBy;

    private Instant createdAt;

    private Instant updatedAt;
}
