package com.JRobusta.chat.core_services.message_module.dtos.Requests;


import com.JRobusta.chat.core_services.message_module.entities.Conversation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateConversationRq {

    private Conversation.ConversationType type;

    private String name;

    private String topic;

    private Boolean isPrivate = false;

    private UUID createdBy;

    private Instant createdAt;

    private Instant updatedAt;
}
