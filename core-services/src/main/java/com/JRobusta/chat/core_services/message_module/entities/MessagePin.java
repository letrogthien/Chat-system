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
@Table(name = "message_pins")
@IdClass(MessagePin.MessagePinId.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessagePin {
  @Id
  @Column(name = "message_id", columnDefinition = "BINARY(16)")
  private UUID messageId;

  @Id
  @Column(name = "pinned_by", columnDefinition = "BINARY(16)")
  private UUID pinnedBy;

  @Column(name = "pinned_at", insertable = false, updatable = false)
  private Instant pinnedAt;

  @AllArgsConstructor
  @NoArgsConstructor
  @Getter
  @Setter
  public static class MessagePinId implements Serializable {
    private UUID messageId;
    private UUID pinnedBy;
  }
}
