package com.JRobusta.chat.core_services.message_module.grpc;

import com.JRobusta.chat.core_services.events.MessageEvent;
import com.JRobusta.chat.core_services.message_module.common.OutboxStatus;
import com.JRobusta.chat.core_services.message_module.entities.Message;
import com.JRobusta.chat.core_services.message_module.entities.MessageProducerOutbox;
import com.JRobusta.chat.core_services.message_module.kafka.KafkaTopic;
import com.JRobusta.chat.core_services.message_module.mapper.MessageMapper;
import com.JRobusta.chat.core_services.message_module.repositories.ConversationSequenceRepository;
import com.JRobusta.chat.core_services.message_module.repositories.MessageProducerOutboxRepository;
import com.JRobusta.chat.core_services.message_module.repositories.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import message.v1.MessageOuterClass;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageServiceHelper {

    private final MessageRepository messageRepository;
    private final MessageProducerOutboxRepository messageProducerOutboxRepository;
    private final ConversationSequenceRepository conversationSequenceRepository;
    private final MessageMapper messageMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public MessageOuterClass.Message handleMessage(MessageOuterClass.Message protoMessage) throws Exception {
        Message messageEntity = messageMapper.toEntity(protoMessage);

        Long sequenceNumber = conversationSequenceRepository
                .getSequenceNumber(messageEntity.getConversationId()) + 1;

        Instant now = Instant.now();
        messageEntity.setMessageId(UUID.randomUUID());
        messageEntity.setServerSeq(sequenceNumber);
        messageEntity.setCreatedAt(now);
        messageEntity.setUpdatedAt(now);
        messageEntity.setDeleted(false);
        messageEntity.setThreadRootId(null);
        Message savedMessage = messageRepository.save(messageEntity);

        conversationSequenceRepository.updateSequenceNumber(
                messageEntity.getConversationId(),
                sequenceNumber
        );

        MessageEvent event = MessageEvent.builder()
                .messageId(savedMessage.getMessageId())
                .conversationId(savedMessage.getConversationId())
                .userId(savedMessage.getUserId())
                .text(savedMessage.getText())
                .serverSeq(savedMessage.getServerSeq())
                .createdAt(savedMessage.getCreatedAt())
                .updatedAt(savedMessage.getUpdatedAt())
                .deleted(savedMessage.getDeleted())
                .threadRootId(savedMessage.getThreadRootId())
                .type(savedMessage.getType())
                .build();

        MessageProducerOutbox outbox = MessageProducerOutbox.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(now)
                .topic(KafkaTopic.MESSAGE_ALL.getTopicName())
                .payload(objectMapper.writeValueAsString(event))
                .status(OutboxStatus.PENDING)
                .conversationId(event.getConversationId())
                .build();

        messageProducerOutboxRepository.save(outbox);

        return messageMapper.toProto(savedMessage);
    }
}
