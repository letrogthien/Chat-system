package com.JRobusta.chat.fanout_worker.grpc;

import com.JRobusta.chat.fanout_worker.common.Const;
import connection.v1.ConnectionManagerGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcConfig {
  @Bean
  ConnectionManagerGrpc.ConnectionManagerBlockingStub connectionManagerBlockingStub(
      GrpcChannelFactory grpcChannelFactor) {
    return ConnectionManagerGrpc.newBlockingStub(
        grpcChannelFactor.createChannel(Const.CHANNELS_GRPC_CONNECTION_MANAGEMENT.getValue()));
  }
}
