package com.JRobusta.chat.core_services.message_module.services;

import com.JRobusta.chat.events.MessageEvent;
import com.JRobusta.chat.core_services.kafka.KafkaTopic;
import com.JRobusta.chat.core_services.kafka.producer.SendEventService;
import com.JRobusta.chat.core_services.message_module.common.OutboxStatus;
import com.JRobusta.chat.core_services.message_module.repositories.MessageProducerOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageOutboxService {
  private final SendEventService sendEventService;
  private final MessageProducerOutboxRepository outboxRepository;
  private final ObjectMapper objectMapper;


  public void markAsProcessed(String id, OutboxStatus status) {
    outboxRepository.updateStatusById(id, status);
  }


  public void trySendOrForward(String nextRetryTopic, long delayMs, String message) {
    try {
      String[] parts = message.split("\\|", 2);
      String uuid = parts[0];
      String payload = parts[1];

      markAsProcessed(uuid, OutboxStatus.PROCESSED);

      MessageEvent messageEvent = objectMapper.readValue(payload, MessageEvent.class);

      sendEventService.sendEvent(KafkaTopic.MESSAGE_ALL.getTopicName(), messageEvent,
          messageEvent.getConversationId().toString());

    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }


  @Transactional
  public void trySendOrForward(String message) {
    try {
      String[] parts = message.split("\\|", 2);
      String uuid = parts[0];
      String payload = parts[1];

      markAsProcessed(uuid, OutboxStatus.PROCESSED);

      MessageEvent messageEvent = objectMapper.readValue(payload, MessageEvent.class);

      sendEventService.sendEvent(KafkaTopic.MESSAGE_ALL.getTopicName(), messageEvent,
          messageEvent.getConversationId().toString());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }


}
