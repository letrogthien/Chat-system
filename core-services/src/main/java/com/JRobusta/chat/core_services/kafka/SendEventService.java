package com.JRobusta.chat.core_services.kafka;


import com.JRobusta.chat.core_services.events.MessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
public class SendEventService {
    private final KafkaTemplate<String, MessageEvent> kafkaTemplateMessageEvent;
    private final KafkaTemplate<String, String> kafkaTemplateString;

    public CompletableFuture<SendResult<String, MessageEvent>> sendEvent(final String topic, final MessageEvent event, final String key) {
        return kafkaTemplateMessageEvent.send(topic, key, event);
    }

    public CompletableFuture<SendResult<String, String>> sendEventOutbox(final String topic, final String value, final String key) {
        return kafkaTemplateString.send(topic, key, value);
    }

}
