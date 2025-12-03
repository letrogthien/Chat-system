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
@Table(name = "message_acks")
@IdClass(MessageAck.MessageAckId.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageAck {
  @Id
  @Column(name = "message_id", columnDefinition = "BINARY(16)")
  private UUID messageId;

  @Id
  @Column(name = "user_id", columnDefinition = "BINARY(16)")
  private UUID userId;

  @Column(name = "delivered_at")
  private Instant deliveredAt;

  @Column(name = "seen_at")
  private Instant seenAt;

  @AllArgsConstructor
  @NoArgsConstructor
  @Getter
  @Setter
  public static class MessageAckId implements Serializable {
    private UUID messageId;
    private UUID userId;
  }
}
