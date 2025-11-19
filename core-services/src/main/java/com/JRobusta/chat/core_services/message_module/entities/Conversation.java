package com.JRobusta.chat.core_services.message_module.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "conversations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Conversation {
    @Id
    @Column(name = "conversation_id", columnDefinition = "BINARY(16)")
    private UUID conversationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ConversationType type;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "topic", length = 255)
    private String topic;

    @Column(name = "is_private")
    private Boolean isPrivate = false;

    @Column(name = "created_by", columnDefinition = "BINARY(16)", nullable = false)
    private UUID createdBy;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

    public enum ConversationType {
        CHANNEL, DM, GROUP_DM
    }
}
