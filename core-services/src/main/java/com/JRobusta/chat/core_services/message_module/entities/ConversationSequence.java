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
@Table(name = "conversation_sequences")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConversationSequence {
  @Id
  @Column(name = "conversation_id", columnDefinition = "BINARY(16)")
  private UUID conversationId;

  @Column(name = "last_seq")
  private Long lastSeq = 0L;

  @Column(name = "updated_at", insertable = false, updatable = false)
  private Instant updatedAt;
}
