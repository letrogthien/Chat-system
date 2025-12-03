package com.JRobusta.chat.socket_gateway.redis;

import com.JRobusta.chat.events.EphemeralRedisMessage;
import com.JRobusta.chat.events.MessageEvent;
import com.JRobusta.chat.socket_gateway.common.Const;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RedisStreamConfig {

    @Value("${gateway.id}")
    private String gatewayNodeId;
    private final StreamListener<String, ObjectRecord<String, String>> redisEphemeralListener;

    @Bean
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> streamContainer(
            RedisConnectionFactory redisConnectionFactory
    ) {


        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .pollTimeout(Duration.ofSeconds(1))
                        .targetType(String.class)
                        .errorHandler(Throwable::printStackTrace)  // log và không chết container
                        .build();

        StreamMessageListenerContainer<String, ObjectRecord<String, String>> container =
                StreamMessageListenerContainer.create(redisConnectionFactory, options);

        container.receiveAutoAck(
                Consumer.from(Const.GROUP_EPHEMERAL_MESSAGE_ALL.getValue(), "1"),
                StreamOffset.create("gatewayId:"+ gatewayNodeId + ":stream", ReadOffset.lastConsumed()),
                redisEphemeralListener
        );

        container.start();
        return container;
    }
}
