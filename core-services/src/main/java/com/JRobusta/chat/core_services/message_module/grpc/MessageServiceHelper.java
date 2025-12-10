package com.JRobusta.chat.core_services.message_module.grpc;

import com.JRobusta.chat.core_services.kafka.producer.SendEventService;
import com.JRobusta.chat.core_services.message_module.entities.*;
import com.JRobusta.chat.core_services.message_module.repositories.*;
import com.JRobusta.chat.events.MessageEvent;
import com.JRobusta.chat.core_services.message_module.common.OutboxStatus;
import com.JRobusta.chat.core_services.kafka.KafkaTopic;
import com.JRobusta.chat.core_services.mapper.MessageMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import message.v1.MessageOuterClass;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageServiceHelper {

  private final MessageRepository messageRepository;
  private final MessageProducerOutboxRepository messageProducerOutboxRepository;
  private final ConversationSequenceRepository conversationSequenceRepository;
  private final ConversationMemberRepository conversationMemberRepository;
  private final MessageMapper messageMapper;
  private final ObjectMapper objectMapper;
  private final ConversationRepository conversationRepository;
  private final SendEventService sendEventService;

  @Transactional
  public MessageOuterClass.Message handleMessage(MessageOuterClass.Message protoMessage)
      throws Exception {


    Message messageEntity = messageMapper.toEntity(protoMessage);

    Long sequenceNumber =
        conversationSequenceRepository.getSequenceNumber(messageEntity.getConversationId()) + 1;

    Instant now = Instant.now();
    messageEntity.setMessageId(UUID.randomUUID());
    messageEntity.setServerSeq(sequenceNumber);
    messageEntity.setCreatedAt(now);
    messageEntity.setUpdatedAt(now);
    messageEntity.setDeleted(false);
    messageEntity.setThreadRootId(null);
    Message savedMessage = messageRepository.save(messageEntity);

    conversationSequenceRepository.updateSequenceNumber(messageEntity.getConversationId(),
        sequenceNumber);

    List<UUID> memberIds = conversationMemberRepository
        .getListMemberIdByConversationId(messageEntity.getConversationId());

    MessageEvent event = MessageEvent.builder().messageId(savedMessage.getMessageId())
        .conversationId(savedMessage.getConversationId()).userId(savedMessage.getUserId())
        .text(savedMessage.getText()).serverSeq(savedMessage.getServerSeq())
        .createdAt(savedMessage.getCreatedAt()).updatedAt(savedMessage.getUpdatedAt())
        .deleted(savedMessage.getDeleted()).threadRootId(savedMessage.getThreadRootId())
        .type(savedMessage.getType()).conversationMemberIds(memberIds).build();

    MessageProducerOutbox outbox = MessageProducerOutbox.builder().id(UUID.randomUUID().toString())
        .createdAt(now).topic(KafkaTopic.MESSAGE_ALL.getTopicName())
        .payload(objectMapper.writeValueAsString(event)).status(OutboxStatus.PENDING)
        .conversationId(event.getConversationId()).build();

    messageProducerOutboxRepository.save(outbox);

    return messageMapper.toProto(savedMessage);
  }


  @Transactional
  public MessageOuterClass.Message handleGuestMessage(MessageOuterClass.Message protoMessage)
  {
    if (protoMessage.getType().equals("GUEST") && !conversationRepository.existsById(
        UUID.fromString(protoMessage.getUserId()))) {
      conversationRepository.save(
          Conversation.builder().conversationId(UUID.fromString(protoMessage.getUserId()))
              .name("GUEST-" + protoMessage.getUserId())
              .createdBy(UUID.fromString(protoMessage.getUserId()))
              .type(Conversation.ConversationType.DM).build());

      conversationMemberRepository.save(ConversationMember.builder()
          .conversationId(UUID.fromString(protoMessage.getUserId()))
          .role(ConversationMember.MemberRole.OWNER).joinedAt(Instant.now())
          .userId(UUID.fromString(protoMessage.getUserId())).build());

      conversationSequenceRepository.save(ConversationSequence.builder()
          .conversationId(UUID.fromString(protoMessage.getUserId())).lastSeq(0L).build());
    }

    // call chat bot here


    Message messageEntity = messageMapper.toEntity(protoMessage);

    Long sequenceNumber =
        conversationSequenceRepository.getSequenceNumber(messageEntity.getConversationId()) + 1;

    Instant now = Instant.now();
    messageEntity.setMessageId(UUID.randomUUID());
    messageEntity.setServerSeq(sequenceNumber);
    messageEntity.setCreatedAt(now);
    messageEntity.setUpdatedAt(now);
    messageEntity.setDeleted(false);
    messageEntity.setThreadRootId(null);
    Message savedMessage = messageRepository.save(messageEntity);

    conversationSequenceRepository.updateSequenceNumber(messageEntity.getConversationId(),
        sequenceNumber);
    MessageEvent event = MessageEvent.builder().messageId(savedMessage.getMessageId())
        .conversationId(savedMessage.getConversationId()).userId(savedMessage.getUserId())
        .text(savedMessage.getText()).serverSeq(savedMessage.getServerSeq())
        .createdAt(savedMessage.getCreatedAt()).updatedAt(savedMessage.getUpdatedAt())
        .deleted(savedMessage.getDeleted()).threadRootId(savedMessage.getThreadRootId())
        .type(savedMessage.getType())
        .conversationMemberIds(List.of(savedMessage.getConversationId())).build();
    sendEventService.sendEvent(KafkaTopic.CHATBOT_MESSAGE.getTopicName(), event,
        event.getConversationId().toString());
    return messageMapper.toProto(savedMessage);
  }


}
