package com.JRobusta.chat.core_services.message_module.entities;

import com.JRobusta.chat.core_services.message_module.common.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "message_producer_outbox")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MessageProducerOutbox {
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "conversation_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID conversationId;

    @Column(name = "topic", length = 255, nullable = false)
    private String topic;

    @Column(name = "payload", columnDefinition = "JSON", nullable = false)
    private String payload;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OutboxStatus status = OutboxStatus.PENDING;


}
