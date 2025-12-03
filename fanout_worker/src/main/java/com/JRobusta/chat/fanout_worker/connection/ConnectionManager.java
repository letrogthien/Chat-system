package com.JRobusta.chat.fanout_worker.connection;

import com.JRobusta.chat.fanout_worker.grpc.ConnectionGrpcClient;
import com.JRobusta.chat.fanout_worker.mapper.ConnectionMapper;
import com.JRobusta.chat.fanout_worker.redis.RedisConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConnectionManager {
  private final ConnectionGrpcClient connectionGrpcClient;
  private final ConnectionMapper connectionMapper;
  private final RedisConnectionService redisConnectionService;


  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    connectionGrpcClient.getAllConnections().stream().map(connectionMapper::toEvent)
        .forEach(redisConnectionService::saveConnectionActive);
  }


}
