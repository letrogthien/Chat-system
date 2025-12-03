package com.JRobusta.chat.core_services.message_module.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "message_reactions")
@IdClass(MessageReaction.MessageReactionId.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageReaction {
  @Id
  @Column(name = "message_id", columnDefinition = "BINARY(16)")
  private UUID messageId;

  @Id
  @Column(name = "emoji", length = 64)
  private String emoji;

  @Id
  @Column(name = "user_id", columnDefinition = "BINARY(16)")
  private UUID userId;

  @Column(name = "reacted_at", insertable = false, updatable = false)
  private Instant reactedAt;

  @AllArgsConstructor
  @NoArgsConstructor
  @Getter
  @Setter
  public static class MessageReactionId implements Serializable {
    private UUID messageId;
    private String emoji;
    private UUID userId;
  }
}
