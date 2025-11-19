package com.JRobusta.chat.core_services.message_module.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "message_versions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "version_id")
    private Long versionId;

    @Column(name = "message_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID messageId;

    @Column(name = "old_text", columnDefinition = "TEXT", nullable = false)
    private String oldText;

    @Column(name = "edited_at", insertable = false, updatable = false)
    private Instant editedAt;

    @Column(name = "edited_by", columnDefinition = "BINARY(16)", nullable = false)
    private UUID editedBy;
}
