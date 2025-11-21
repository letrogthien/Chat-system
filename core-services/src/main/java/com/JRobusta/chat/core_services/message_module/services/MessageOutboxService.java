package com.JRobusta.chat.core_services.message_module.services;

import com.JRobusta.chat.core_services.events.MessageEvent;
import com.JRobusta.chat.core_services.message_module.common.OutboxStatus;
import com.JRobusta.chat.core_services.message_module.entities.MessageProducerOutbox;
import com.JRobusta.chat.core_services.message_module.kafka.KafkaTopic;
import com.JRobusta.chat.core_services.message_module.kafka.SendEventService;
import com.JRobusta.chat.core_services.message_module.repositories.MessageProducerOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageOutboxService {
    private final SendEventService sendEventService;
    private final MessageProducerOutboxRepository outboxRepository;

    @Transactional
    public void processOutboxEvent(MessageEvent messageEvent, String outboxId) {
        MessageProducerOutbox outbox = outboxRepository.findById(outboxId)
                .orElseThrow(() -> new IllegalStateException("Outbox entry not found: " + outboxId));

        if (outbox.getStatus() == OutboxStatus.PROCESSED) {
            return;
        }

        sendEventService.sendEvent(
                KafkaTopic.MESSAGE_ALL.getTopicName(),
                messageEvent,
                messageEvent.getConversationId().toString()
        );

        outbox.setStatus(OutboxStatus.PROCESSED);
        outbox.setProcessedAt(Instant.now());
        outboxRepository.save(outbox);

    }
}
