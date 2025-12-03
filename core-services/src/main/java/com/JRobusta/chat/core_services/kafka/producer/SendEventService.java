package com.JRobusta.chat.core_services.kafka.producer;


import com.JRobusta.chat.events.ConnectionEvent;
import com.JRobusta.chat.events.MessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
public class SendEventService {
  private final KafkaTemplate<String, MessageEvent> kafkaTemplateMessageEvent;
  private final KafkaTemplate<String, ConnectionEvent> kafkaTemplateConnectionEvent;
  private final KafkaTemplate<String, String> kafkaTemplateString;

  public void sendEvent(final String topic,
                        final MessageEvent event, final String key) {
    kafkaTemplateMessageEvent.send(topic, key, event);
  }

  public CompletableFuture<SendResult<String, String>> sendEventOutbox(final String topic,
      final String value, final String key) {
    return kafkaTemplateString.send(topic, key, value);
  }


  public void sendEventConnection(final String topic,
                                  final ConnectionEvent value) {
    kafkaTemplateConnectionEvent.send(topic, value);
  }

}
