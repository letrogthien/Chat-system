package com.JRobusta.chat.core_services.connection_management.repositories;

import com.JRobusta.chat.core_services.connection_management.common.Const;
import com.google.protobuf.util.Timestamps;
import connection.v1.ConnectionManagerOuterClass;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class ConnectionRepository {
    private final RedisTemplate<String, Object> redisTemplate;


    public void saveHashConnection(ConnectionManagerOuterClass.Connection connection) {
        String key = Const.CONNECTION_VERIFIED_HASH + connection.getConnectionId();

        Map<String, Object> map = new HashMap<>();
        map.put("connection_id", connection.getConnectionId());
        map.put("user_id", connection.getUserId());
        map.put("workspace_id", connection.getWorkspaceId());
        map.put("gateway_node_id", connection.getGatewayNodeId());
        map.put("connected_at", Timestamps.toMillis(connection.getConnectedAt()));
        map.put("last_ping_at", Timestamps.toMillis(connection.getLastPingAt()));
        map.put("device_type", connection.getDeviceType());
        map.put("ip_address", connection.getIpAddress());
        map.put("session_state", connection.getSessionState().name());

        redisTemplate.opsForHash().putAll(key, map);
    }

    public void deleteHashConnection(String connectionId) {
        String key = Const.CONNECTION_VERIFIED_HASH + connectionId;
        redisTemplate.delete(key);
    }

    public void saveSetConnection(String userId, String connectionId) {
        redisTemplate.opsForSet().add(Const.CONNECTION_VERIFIED_SET.getValue() + userId, connectionId);
    }

    public void deleteSetConnection(String userId, String connectionId) {
        redisTemplate.opsForSet().remove(Const.CONNECTION_VERIFIED_SET.getValue() + userId, connectionId);
    }

    public List<ConnectionManagerOuterClass.Connection> getConnections(String userId) {
        System.out.println("Fetching connections for userId: " + userId);
        return Objects.requireNonNull(redisTemplate.opsForSet().members(Const.CONNECTION_VERIFIED_SET.getValue() + userId)).stream().map(
                connectionId -> {
                    String key = Const.CONNECTION_VERIFIED_HASH + connectionId.toString();
                    Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
                    return getConnection(entries);
                }
        ).toList();
    }

    public ConnectionManagerOuterClass.Connection getConnection(String connectionId) {
        String key = Const.CONNECTION_VERIFIED_HASH + connectionId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            return null;
        }
        return getConnection(entries);
    }
    public void updateFieldHash(String connectionId, String field, Object value) {
        String key = Const.CONNECTION_VERIFIED_HASH + connectionId;
        redisTemplate.opsForHash().put(key, field, value);
    }

    private ConnectionManagerOuterClass.Connection getConnection(Map<Object, Object> entries) {
        return ConnectionManagerOuterClass.Connection.newBuilder()
                .setConnectionId((String) entries.get("connection_id"))
                .setUserId((String) entries.get("user_id"))
                .setWorkspaceId((String) entries.get("workspace_id"))
                .setGatewayNodeId((String) entries.get("gateway_node_id"))
                .setConnectedAt(
                        com.google.protobuf.util.Timestamps.fromMillis(
                                Long.parseLong(entries.get("connected_at").toString())
                        )
                )
                .setLastPingAt(
                        com.google.protobuf.util.Timestamps.fromMillis(
                                Long.parseLong(entries.get("last_ping_at").toString())
                        )
                )
                .setDeviceType((String) entries.get("device_type"))
                .setIpAddress((String) entries.get("ip_address"))
                .setSessionState(
                        ConnectionManagerOuterClass.SessionState.valueOf((String) entries.get("session_state"))
                )
                .build();
    }


}
