package com.JRobusta.chat.fanout_worker.schedule;


import com.JRobusta.chat.fanout_worker.grpc.ConnectionGrpcClient;
import com.JRobusta.chat.fanout_worker.mapper.ConnectionMapper;
import com.JRobusta.chat.fanout_worker.redis.RedisConnectionService;
import connection.v1.ConnectionManagerOuterClass;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConnectionRefresh {
  private final RedisConnectionService redisService;
  private final ConnectionMapper connectionMapper;
  private final ConnectionGrpcClient connectionGrpcClient;

  @Scheduled(fixedRate = 300000) 
  public void refreshConnections() {
    List<ConnectionManagerOuterClass.Connection> connections = 
        connectionGrpcClient.getAllConnections();
    redisService.removeALlConnectionActive();
    connections.stream().map(connectionMapper::toEvent).forEach(redisService::saveConnectionActive);
  }
}

