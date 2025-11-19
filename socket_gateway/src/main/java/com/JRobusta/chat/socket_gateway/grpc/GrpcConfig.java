package com.JRobusta.chat.socket_gateway.grpc;

import connection.v1.ConnectionManagerGrpc;
import message.v1.MessageServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

import com.JRobusta.chat.socket_gateway.common.Const;

import auth.v1.AuthServiceGrpc;
import auth.v1.AuthServiceGrpc.AuthServiceBlockingStub;

@Configuration

public class GrpcConfig {
    @Bean
    AuthServiceBlockingStub authServiceBlockingStub(GrpcChannelFactory grpcChannelFactory) {
        return AuthServiceGrpc.newBlockingStub(grpcChannelFactory.createChannel(Const.CHANNELS_GRPC_AUTH.getValue()));
    }

    @Bean
    ConnectionManagerGrpc.ConnectionManagerBlockingStub connectionManagerBlockingStub(GrpcChannelFactory grpcChannelFactory) {
        return ConnectionManagerGrpc.newBlockingStub(grpcChannelFactory.createChannel(Const.CHANNELS_GRPC_CONNECTION_MANAGEMENT.getValue()));
    }


    @Bean
    MessageServiceGrpc.MessageServiceBlockingStub messageServiceBlockingStub(GrpcChannelFactory grpcChannelFactory) {
        return MessageServiceGrpc.newBlockingStub(grpcChannelFactory.createChannel(Const.CHANNELS_GRPC_MESSAGE.getValue()));
    }

    @Bean
    MessageServiceGrpc.MessageServiceStub messageServiceStub(GrpcChannelFactory grpcChannelFactory) {
        return MessageServiceGrpc.newStub(grpcChannelFactory.createChannel(Const.CHANNELS_GRPC_MESSAGE.getValue()));
    }


}
