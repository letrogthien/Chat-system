package com.JRobusta.chat.fanout_worker.redis;

import com.JRobusta.chat.events.EphemeralRedisMessage;
import com.JRobusta.chat.events.MessageEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisEphemeralService {
  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper;

  // XADD equivalent for ephemeral data REDIS stream
  public String saveEphemeralData(String gatewayId, EphemeralRedisMessage value) throws JsonProcessingException {
    String streamKey = "gatewayId:"+ gatewayId + ":stream";
    String json = objectMapper.writeValueAsString(value);
    RecordId recordId = redisTemplate.opsForStream()
        .add(StreamRecords.newRecord().in(streamKey).ofMap(Map.of("data", json)
        ), RedisStreamCommands.XAddOptions.maxlen(10000).approximateTrimming(true));
    return recordId != null ? recordId.getValue() : null;
  }
}
