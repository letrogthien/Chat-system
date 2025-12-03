package com.JRobusta.chat.core_services.kafka.consumer;


import com.JRobusta.chat.core_services.message_module.services.MessageOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventConsumer {
  private final MessageOutboxService messageOutboxService;

  @KafkaListener(topics = "outbox.event.raw", groupId = "core-services-group", concurrency = "3")
  @Transactional
  public void consumeOutboxEvent(String message) {
    messageOutboxService.trySendOrForward(message);
  }



}
