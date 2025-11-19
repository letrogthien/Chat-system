package com.JRobusta.chat.core_services.message_module.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "messages")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Message {
    @Id
    @Column(name = "message_id", columnDefinition = "BINARY(16)")
    private UUID messageId;

    @Column(name = "conversation_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID conversationId;

    @Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID userId;

    @Column(name = "server_seq", nullable = false)
    private Long serverSeq;

    @Column(name = "thread_root_id", columnDefinition = "BINARY(16)")
    private UUID threadRootId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(name = "type", length = 20)
    private String type = "default";

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    private Boolean edited = false;
    private Boolean deleted = false;
}
