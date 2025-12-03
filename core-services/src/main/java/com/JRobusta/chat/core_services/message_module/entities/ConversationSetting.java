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
@Table(name = "conversation_settings")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConversationSetting {
  @Id
  @Column(name = "conversation_id", columnDefinition = "BINARY(16)")
  private UUID conversationId;

  @Column(name = "allow_reactions")
  private Boolean allowReactions = true;

  @Column(name = "allow_pins")
  private Boolean allowPins = true;

  @Column(name = "allow_thread")
  private Boolean allowThread = true;

  @Column(name = "slow_mode_seconds")
  private Integer slowModeSeconds = 0;

  @Column(name = "updated_at", insertable = false, updatable = false)
  private Instant updatedAt;
}
