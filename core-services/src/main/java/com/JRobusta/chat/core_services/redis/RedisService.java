package com.JRobusta.chat.core_services.redis;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> stringRedisTemplate;


    public void pushToList(String listKey, String value) {
        stringRedisTemplate.opsForList().rightPush(listKey, value);
    }


    public List<String> popBatchFromList(String key, int batchSize){
        List<Object> results = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (int i = 0; i < batchSize; i++) {
                connection.lPop(key.getBytes());
            }
            return null;
        });

        return results.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .toList();
    }
    public String popFromFallbackToProcessing(String fallbackListKey, String processingListKey) {
        return stringRedisTemplate.opsForList().rightPopAndLeftPush(fallbackListKey, processingListKey);
    }

    public void removeFromProcessing(String processingListKey, String value) {
        stringRedisTemplate.opsForList().remove(processingListKey, 1, value);
    }
}
