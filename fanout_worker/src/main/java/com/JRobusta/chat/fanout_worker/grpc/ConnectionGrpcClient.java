package com.JRobusta.chat.fanout_worker.grpc;

import connection.v1.ConnectionManagerGrpc;
import connection.v1.ConnectionManagerOuterClass;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConnectionGrpcClient {
  private final ConnectionManagerGrpc.ConnectionManagerBlockingStub stub;

  public List<ConnectionManagerOuterClass.Connection> getAllConnections() {
    ConnectionManagerOuterClass.GetConnectionsByGatewayNodeResponse connections =
        stub.getAllConnections(
            ConnectionManagerOuterClass.GetAllConnectionsRequest.newBuilder().build());
    return connections.getConnectionsList();
  }
}
