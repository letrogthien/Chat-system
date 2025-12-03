package com.JRobusta.chat.fanout_worker.kafka.consumer;


import com.JRobusta.chat.events.ConnectionEvent;
import com.JRobusta.chat.events.EphemeralRedisMessage;
import com.JRobusta.chat.events.MessageEvent;
import com.JRobusta.chat.fanout_worker.redis.RedisConnectionService;
import com.JRobusta.chat.fanout_worker.redis.RedisEphemeralService;
import com.JRobusta.chat.fanout_worker.services.MessageService;
import connection.v1.ConnectionManagerOuterClass;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageConsumer {

  private final MessageService messageService;
  private final RedisEphemeralService redisEphemeralService;
  private final RedisConnectionService redisConnectionService;

  Map<Integer, ExecutorService> partitionExecutors = new ConcurrentHashMap<>();

  @KafkaListener(topics = "message.all", groupId = "fanout-worker-group")
  public void consumeMessageAll(ConsumerRecord<String, MessageEvent> record) {
    System.out.println(record.value());
    ExecutorService executor = partitionExecutors.computeIfAbsent(record.partition(), k -> Executors
        .newSingleThreadExecutor(Thread.ofVirtual().name("vt-chat-" + k, 0).factory()));

    executor.submit(() -> {
      try {
        MessageEvent messageEvent = record.value();
        List<UUID> memberIds = messageEvent.getConversationMemberIds();
        for (UUID memberId : memberIds) {
          Set<ConnectionEvent> connections =
              redisConnectionService.getConnectionsByUserId(String.valueOf(memberId));
          Set<String> gatewayIds = new HashSet<>();
          connections.forEach(connection ->
            gatewayIds.add(connection.getGatewayNodeId())
          );
            for (String gatewayId : gatewayIds) {
                EphemeralRedisMessage ephemeralMessage = EphemeralRedisMessage.builder()
                        .messageEvent(messageEvent)
                        .recipientId(memberId.toString())
                        .build();
                redisEphemeralService.saveEphemeralData(gatewayId, ephemeralMessage);
            }
        }
      } catch (Exception e) {
        log.error("Error processing message for partition {}: {}", record.partition(),
            e.getMessage());
      }
    });
  }



  @PreDestroy
  public void shutdownExecutors() {
    partitionExecutors.values().forEach(ExecutorService::shutdown);
  }


}
