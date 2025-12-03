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
@Table(name = "conversation_members")
@IdClass(ConversationMember.ConversationMemberId.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConversationMember {
  @Id
  @Column(name = "conversation_id", columnDefinition = "BINARY(16)")
  private UUID conversationId;

  @Id
  @Column(name = "user_id", columnDefinition = "BINARY(16)")
  private UUID userId;

  @Column(name = "joined_at", insertable = false, updatable = false)
  private Instant joinedAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "role")
  private MemberRole role = MemberRole.MEMBER;

  public enum MemberRole {
    OWNER, MEMBER
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Getter
  @Setter
  public static class ConversationMemberId implements Serializable {
    private UUID conversationId;
    private UUID userId;
  }
}
