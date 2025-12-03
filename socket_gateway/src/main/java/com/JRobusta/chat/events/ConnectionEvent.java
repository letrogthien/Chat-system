package com.JRobusta.chat.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConnectionEvent {
    private String connectionId;
    private String userId;
    private String workspaceId;
    private String gatewayNodeId;
    private Instant connectedAt;
    private Instant lastPingAt;
    private String deviceType;
    private String ipAddress;
    private SessionState sessionState;



    public enum SessionState {
        SESSION_STATE_UNKNOWN,
        CONNECTED,
        DISCONNECTED,
        EXPIRED
    }
}
