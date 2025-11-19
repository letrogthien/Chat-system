package com.JRobusta.chat.core_services.message_module.kafka;


import com.JRobusta.chat.core_services.events.MessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
public class SendEventService {
    private final KafkaTemplate<String, MessageEvent> kafkaTemplate;

    public CompletableFuture<SendResult<String, MessageEvent>> sendEvent(final String topic, final MessageEvent event, final String key) {
        return kafkaTemplate.send(topic, key, event);
    }

}
