package com.JRobusta.chat.socket_gateway.redis;


import com.JRobusta.chat.events.EphemeralRedisMessage;
import com.JRobusta.chat.socket_gateway.socket.SocketSendMsgService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisEphemeralListener implements StreamListener<String, ObjectRecord<String, String>> {
    private final SocketSendMsgService socketSendMsgService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        try {
            String json = message.getValue();
            EphemeralRedisMessage ephemeralMessage = objectMapper.readValue(json, EphemeralRedisMessage.class);
            socketSendMsgService.sendMessageToUser(ephemeralMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
