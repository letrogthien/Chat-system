package com.JRobusta.chat.fanout_worker.redis;


import com.JRobusta.chat.events.ConnectionEvent;
import com.JRobusta.chat.fanout_worker.common.Const;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
@RequiredArgsConstructor
public class RedisConnectionService {
  private final RedisTemplate<String, String> stringRedisTemplate;
  private final RedisTemplate<String, ConnectionEvent> connectionEventRedisTemplate;

  public void saveConnectionActive(ConnectionEvent connection) {
    String key = Const.CONNECTION_ACTIVE.getValue() + connection.getConnectionId();
    String keySet = Const.CONNECTION_SET.getValue() + connection.getUserId();
    stringRedisTemplate.opsForSet().add(keySet, connection.getConnectionId());
    connectionEventRedisTemplate.opsForValue().set(key, connection);
  }
  public void removeConnectionActive(ConnectionEvent connection) {
    String key = Const.CONNECTION_ACTIVE.getValue() + connection.getConnectionId();
    String keySet = Const.CONNECTION_SET.getValue() + connection.getUserId();
    stringRedisTemplate.opsForSet().remove(keySet, connection.getConnectionId());
    connectionEventRedisTemplate.delete(key);
  }

  public void removeALlConnectionActive() {
    Set<String> keys = stringRedisTemplate.keys(Const.CONNECTION_ACTIVE.getValue() + "*");
    if (!keys.isEmpty()) {
      connectionEventRedisTemplate.delete(keys);
    }

    Set<String> keySet = stringRedisTemplate.keys(Const.CONNECTION_SET.getValue() + "*");
    if (!keySet.isEmpty()) {
      stringRedisTemplate.delete(keySet);
    }
  }


  public ConnectionEvent getConnectionActive() {
    Set<String> keys = stringRedisTemplate.keys(Const.CONNECTION_ACTIVE.getValue() + "*");
    if (!keys.isEmpty()) {
      String key = keys.iterator().next();
      return  connectionEventRedisTemplate.opsForValue().get(key);
    }
    return null;
  }

  public ConnectionEvent getConnectionById(String connectionId) {
    String key = Const.CONNECTION_ACTIVE.getValue() + connectionId;
    return connectionEventRedisTemplate.opsForValue().get(key);
  }

  public Set<ConnectionEvent> getConnectionsByUserId(String userId) {
    String keySet = Const.CONNECTION_SET.getValue() + userId;
    Set<String> connectionIds = stringRedisTemplate.opsForSet().members(keySet);
    Set<ConnectionEvent> connections = new java.util.HashSet<>();
    if (connectionIds != null) {
      for (String connectionId : connectionIds) {
        ConnectionEvent connection = getConnectionById(connectionId);
        if (connection != null) {
          connections.add(connection);
        }
      }
    }
    return connections;
  }




}
