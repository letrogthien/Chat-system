package com.JRobusta.chat.core_services.message_module.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "message_attachments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageAttachment {
  @Id
  @Column(name = "attachment_id", columnDefinition = "BINARY(16)")
  private UUID attachmentId;

  @Column(name = "message_id", columnDefinition = "BINARY(16)", nullable = false)
  private UUID messageId;

  @Column(name = "type", length = 30, nullable = false)
  private String type;

  @Column(name = "url", columnDefinition = "TEXT")
  private String url;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "metadata", columnDefinition = "JSON")
  private String metadata;

  @Column(name = "created_at", insertable = false, updatable = false)
  private Instant createdAt;
}
