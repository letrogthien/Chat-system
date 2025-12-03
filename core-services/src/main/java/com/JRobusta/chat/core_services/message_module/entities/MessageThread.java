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
@Table(name = "message_threads")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageThread {
  @Id
  @Column(name = "thread_root_id", columnDefinition = "BINARY(16)")
  private UUID threadRootId;

  @Column(name = "conversation_id", columnDefinition = "BINARY(16)", nullable = false)
  private UUID conversationId;

  @Column(name = "reply_count")
  private Integer replyCount = 0;

  @Column(name = "last_reply_at", insertable = false, updatable = false)
  private Instant lastReplyAt;

  @Column(name = "last_replied_by", columnDefinition = "BINARY(16)")
  private UUID lastRepliedBy;
}
