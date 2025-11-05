package com.JRobusta.chat.socket_gateway.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.JRobusta.chat.socket_gateway.common.Const;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate <String, Object> redisTemplate;
    public void saveConnectionVerified(String connectionId, Boolean isVerified){
        redisTemplate.opsForValue().set(Const.CONNECTION_VERIFIED_REDIS.getValue() + connectionId, isVerified);
    }

    public void deleteConnectionVerified(String connectionId){
        redisTemplate.delete(Const.CONNECTION_VERIFIED_REDIS.getValue() + connectionId);
    }


    public Boolean getConnectionVerified(String connectionId){
        return (Boolean) redisTemplate.opsForValue().get(Const.CONNECTION_VERIFIED_REDIS.getValue() + connectionId);
    }

}
