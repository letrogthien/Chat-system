package com.JRobusta.chat.fanout_worker.kafka.consumer;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.JRobusta.chat.events.ConnectionEvent;
import com.JRobusta.chat.fanout_worker.mapper.ConnectionMapper;
import com.JRobusta.chat.fanout_worker.redis.RedisConnectionService;

import connection.v1.ConnectionManagerOuterClass;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConnectionConsumer {
    private final RedisConnectionService redisService;
    private final ConnectionMapper connectionMapper;

    @KafkaListener(topics = "connection.add", groupId = "connection-fanoutworker", concurrency = "3")
    public void consumeConnectionAddEvent(ConnectionEvent event) {

        redisService.saveConnectionActive(event);
    }

    @KafkaListener(topics = "connection.remove", groupId = "connection-fanoutworker", concurrency = "3")
    public void consumeConnectionRemoveEvent(ConnectionEvent event) {

        redisService.removeConnectionActive(event);
    }
}
