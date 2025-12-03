package com.JRobusta.chat.socket_gateway.grpc.client;

import connection.v1.ConnectionManagerGrpc;
import connection.v1.ConnectionManagerOuterClass;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ConnectionManagerClient {
    private final ConnectionManagerGrpc.ConnectionManagerBlockingStub stub;

    public boolean saveConnection(ConnectionManagerOuterClass.Connection connection, String userId) {
        try {
            ConnectionManagerOuterClass.RegisterConnectionResponse res =
                    stub.withDeadlineAfter(5, TimeUnit.SECONDS)
                            .registerConnection(
                                    ConnectionManagerOuterClass.RegisterConnectionRequest
                                            .newBuilder()
                                            .setUserId(userId)
                                            .setConnection(connection)
                                            .build()
                            );
            return res.getSuccess();

        } catch (io.grpc.StatusRuntimeException e) {
            return false;

        }
    }


    public boolean removeConnection(String connectionId) {
        try {
            System.out.println("Removing connection with ID: " + connectionId);
            ConnectionManagerOuterClass.UnregisterConnectionResponse response =
                    stub.unregisterConnection(
                            ConnectionManagerOuterClass.UnregisterConnectionRequest.newBuilder()
                                    .setConnectionId(connectionId)
                                    .build()
                    );
            return response.getSuccess();
        } catch (io.grpc.StatusRuntimeException e) {
            System.out.println("Failed to remove connection with ID: " + connectionId + ". Error: " + e.getMessage());
            return false;
        }
    }
}
