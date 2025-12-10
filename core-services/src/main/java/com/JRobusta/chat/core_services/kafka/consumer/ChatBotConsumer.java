package com.JRobusta.chat.core_services.kafka.consumer;


import com.JRobusta.chat.core_services.kafka.KafkaTopic;
import com.JRobusta.chat.core_services.langchain4j.service.ChatBotService;
import com.JRobusta.chat.core_services.message_module.common.OutboxStatus;
import com.JRobusta.chat.core_services.message_module.entities.Message;
import com.JRobusta.chat.core_services.message_module.entities.MessageProducerOutbox;
import com.JRobusta.chat.core_services.message_module.repositories.ConversationSequenceRepository;
import com.JRobusta.chat.core_services.message_module.repositories.MessageProducerOutboxRepository;
import com.JRobusta.chat.core_services.message_module.repositories.MessageRepository;
import com.JRobusta.chat.events.MessageEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatBotConsumer {
    private final ChatBotService chatBotService;
    private final ConversationSequenceRepository conversationSequenceRepository;
    private final ObjectMapper objectMapper;
    private final Executor virtualThreadExecutor;
    private final MessageRepository messageRepository;
    private final MessageProducerOutboxRepository messageProducerOutboxRepository;

    @KafkaListener(topics = "chatbot.message", groupId = "chatbot-group")
    public void listen(MessageEvent message) {

        virtualThreadExecutor.execute(() -> {
            try {
                String text = message.getText();
                String response = chatBotService.chat(message.getUserId().toString(), text);

                log.info("ChatBot response: {}", response);
                Long sequenceNumber =
                        conversationSequenceRepository.getSequenceNumber(message.getConversationId()) + 1;
                Message messageEntity = Message.builder()
                        .messageId(UUID.randomUUID())
                        .conversationId(message.getConversationId())
                        .userId(message.getUserId())
                        .messageId(UUID.randomUUID())
                        .serverSeq(sequenceNumber)
                        .type("GUEST")
                        .createdAt(Instant.now())
                        .text(response)
                        .build();

                Message savedMessage = messageRepository.save(messageEntity);

                conversationSequenceRepository.updateSequenceNumber(savedMessage.getConversationId(), sequenceNumber);
                MessageEvent event = MessageEvent.builder().messageId(message.getMessageId())
                        .conversationId(message.getConversationId()).userId(message.getUserId())
                        .text(response).serverSeq(savedMessage.getServerSeq())
                        .createdAt(savedMessage.getCreatedAt()).updatedAt(savedMessage.getUpdatedAt())
                        .deleted(savedMessage.getDeleted()).threadRootId(savedMessage.getThreadRootId())
                        .type(savedMessage.getType()).conversationMemberIds(List.of(savedMessage.getUserId())).build();

                MessageProducerOutbox outbox = MessageProducerOutbox.builder().id(UUID.randomUUID().toString())
                        .createdAt(Instant.now()).topic(KafkaTopic.MESSAGE_ALL.getTopicName())
                        .payload(objectMapper.writeValueAsString(event)).status(OutboxStatus.PENDING)
                        .conversationId(event.getConversationId()).build();
                messageProducerOutboxRepository.save(outbox);
            } catch (Exception e) {
                log.error("Error processing message", e);
            }
        });

    }


}
