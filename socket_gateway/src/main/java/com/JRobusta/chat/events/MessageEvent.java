package com.JRobusta.chat.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageEvent {

  private UUID messageId;

  private UUID conversationId;

  private UUID userId;

  private Long serverSeq;

  private UUID threadRootId;

  private String text;

  private String type = "default";

  private Instant createdAt;

  private Instant updatedAt;

  private Boolean edited = false;

  private Boolean deleted = false;

  private List<UUID> conversationMemberIds;
}
