package com.JRobusta.chat.core_services.connection_management.grpc;

import com.JRobusta.chat.core_services.connection_management.repositories.ConnectionRepository;
import connection.v1.ConnectionManagerGrpc;
import connection.v1.ConnectionManagerOuterClass;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConnectionManager extends ConnectionManagerGrpc.ConnectionManagerImplBase {
    private final ConnectionRepository connectionRepository;

    @Override
    public void registerConnection(
            ConnectionManagerOuterClass.RegisterConnectionRequest request,
            StreamObserver<ConnectionManagerOuterClass.RegisterConnectionResponse> responseObserver
    ) {
        try {
            if (request.getUserId().isEmpty() || request.getConnection().getUserId().isEmpty()) {
                responseObserver.onNext(
                        ConnectionManagerOuterClass.RegisterConnectionResponse.newBuilder()
                                .setSuccess(false)
                                .build());
                responseObserver.onCompleted();
            }
            connectionRepository.saveHashConnection(request.getConnection());
            connectionRepository.saveSetConnection(request.getUserId(), request.getConnection().getConnectionId());
            responseObserver.onNext(
                    ConnectionManagerOuterClass.RegisterConnectionResponse.newBuilder()
                            .setSuccess(true)
                            .build());
            responseObserver.onCompleted();
        } catch ( io.grpc.StatusRuntimeException e ) {
            responseObserver.onNext(
                    ConnectionManagerOuterClass.RegisterConnectionResponse.newBuilder()
                            .setSuccess(false)
                            .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void unregisterConnection(
            ConnectionManagerOuterClass.UnregisterConnectionRequest request,
            StreamObserver<ConnectionManagerOuterClass.UnregisterConnectionResponse> responseObserver
    ) {
        try {
            ConnectionManagerOuterClass.Connection connection = connectionRepository.getConnection(request.getConnectionId());
            if (connection == null || connection.getUserId().isEmpty()) {
                System.out.println("Connection not found or userId is empty for connectionId: " + request.getConnectionId());
                responseObserver.onNext(
                        ConnectionManagerOuterClass.UnregisterConnectionResponse.newBuilder()
                                .setSuccess(false)
                                .build());
                responseObserver.onCompleted();
            }
            String userId = connection.getUserId();
            connectionRepository.deleteHashConnection(request.getConnectionId());
            connectionRepository.deleteSetConnection(userId, request.getConnectionId());
            System.out.println("Unregistered connectionId: " + request.getConnectionId() + " for userId: " + userId);
            responseObserver.onNext(
                    ConnectionManagerOuterClass.UnregisterConnectionResponse.newBuilder()
                            .setSuccess(true)
                            .build());
            responseObserver.onCompleted();
        } catch (io.grpc.StatusRuntimeException e) {
            System.out.println("Failed to unregister connectionId: " + request.getConnectionId() + " due to exception: " + e.getMessage());
            responseObserver.onNext(
                    ConnectionManagerOuterClass.UnregisterConnectionResponse.newBuilder()
                            .setSuccess(false)
                            .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void updateLastPing(
            ConnectionManagerOuterClass.UpdateLastPingRequest request,
            StreamObserver<ConnectionManagerOuterClass.UpdateLastPingResponse> responseObserver
    ) {
        connectionRepository.updateFieldHash(request.getConnectionId(), "last_ping_at", request.getLastPingAt());
        responseObserver.onNext(
                ConnectionManagerOuterClass.UpdateLastPingResponse.newBuilder()
                        .setSuccess(true)
                        .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getConnectionsByUser(
            ConnectionManagerOuterClass.GetConnectionsByUserRequest request,
            StreamObserver<ConnectionManagerOuterClass.GetConnectionsByUserResponse> responseObserver
    ) {

        List<ConnectionManagerOuterClass.Connection> connections = connectionRepository.getConnections(request.getUserId());
        responseObserver.onNext(
                ConnectionManagerOuterClass.GetConnectionsByUserResponse.newBuilder()
                        .addAllConnections(connections)
                        .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getOnlineUsers(
            ConnectionManagerOuterClass.GetOnlineUsersRequest request,
            StreamObserver<ConnectionManagerOuterClass.GetOnlineUsersResponse> responseObserver
    ) {
        super.getOnlineUsers(request, responseObserver);
    }
}
